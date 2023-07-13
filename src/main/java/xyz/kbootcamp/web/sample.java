package xyz.kbootcamp.web;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;
import javax.net.ssl.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

@RestController
public class sample {

    @GetMapping("/horseList")
    public Map<String, Object> getTrainerAndHorseList(@RequestParam("jo") String jo) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        long start = System.currentTimeMillis();

        Map<String, Object> data = new HashMap<>();
        String trNo = getTrainerNo(jo); // get the trainer number from the trainer name

        // Get the horse list
        Map<String, Object> horseListData = getHorseData(trNo);
        if (horseListData == null) {
            data.put("error", "Failed to retrieve horse data");
            return data;
        }

        List<Map<String, Object>> horseList = new ArrayList<>();
        List<String> horseMabunList = (List<String>) horseListData.get("horse_list");

        ExecutorService executor = Executors.newFixedThreadPool(10); // use a thread pool with 10 threads

        List<Future<Map<String, Object>>> futures = new ArrayList<>();
        for (int i = 0; i < horseMabunList.size(); i++) {
            String horseName = horseMabunList.get(i).split(" ")[0];
            String horseMabun = horseMabunList.get(i).split(" ")[1];

            Callable<Map<String, Object>> task = () -> {
                Map<String, String> medicalData = getMedicalHistory(horseMabun);

                // Add the disease information to the horse data
                Map<String, Object> horseData = new HashMap<>();
                horseData.put("name", horseName);
                horseData.put("mabun", horseMabun);
                horseData.put("latest_date", medicalData.get("latest_date"));

                return horseData;
            };
            Future<Map<String, Object>> future = executor.submit(task);
            futures.add(future);
        }

        for (Future<Map<String, Object>> future : futures) {
            try {
                Map<String, Object> horseData = future.get();
                horseList.add(horseData);
            } catch (InterruptedException | ExecutionException e) {
                // handle the exception
            }
        }

        executor.shutdown();

        // Add the horse list and trainer name to the data map
        data.put("horse_list", horseList);
        data.put("trainer_name", horseListData.get("trainer_name"));
        long end = System.currentTimeMillis();
        System.out.println("리스트 생성 시간 : " + (end - start)/1000.0);

        return data;
    }


    @GetMapping("/trainers")
    public String getTrainerNo(@RequestParam("trainerName") String trainerName) throws IOException {
        String trainerNo = null;

        String url = "https://race.kra.co.kr/trainer/profileTrainerList.do?Act=10&Sub=1&meet=3";
        Document doc = Jsoup.connect(url).get();

        // Find the row containing the desired trainer name
        Element row = doc.select("table tbody tr")
                .stream()
                .filter(tr -> tr.select("td").get(2).text().equals(trainerName))
                .findFirst()
                .orElse(null);

        if (row != null) {
            // Extract the trainer number from the onclick attribute of the corresponding link
            Element link = row.selectFirst("a[href^=\"#KRA\"][onclick^=\"javascript:goPage2('\"]");
            String onclick = link.attr("onclick");
            String number = onclick.substring(onclick.indexOf("(") + 1, onclick.indexOf(")"));
            number = number.replaceAll("'", "");
            trainerNo = number;
        }

        return trainerNo;
    }

    @PostMapping("/horses")
    public Map<String, Object> getHorseData(String trNo) throws IOException {
        String url = "https://race.kra.co.kr/trainer/TrainerTrustManagehorse.do";
        Connection.Response response = Jsoup.connect(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .data("meet", "3")
                .data("trNo", trNo)
                .method(Connection.Method.POST)
                .execute();

        Document doc = response.parse();

        String trainerName = doc.select(".tableType1 td").get(0).text();

        Elements rows = doc.select(".tableType2 tbody tr");
        Elements horseNoRows = doc.select("a[href^=\"#KRA\"][onclick^=\"javascript:goPage5('\"]");

        List<String> horseList = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            String horseName = row.select("td").get(1).text();
            String onclick = horseNoRows.get(i).attr("onclick");
            String number = onclick.substring(onclick.indexOf("(") + 1, onclick.indexOf(")"));
            number = number.replaceAll("'", "");
            horseList.add(horseName+" "+number);
        }

        Map<String, Object> horseData = new HashMap<>();
        horseData.put("trainer_name", trainerName);
        horseData.put("horse_list", horseList);

        return horseData;
    }

    @GetMapping("/medical-history/{mabun}")
    public Map<String, String> getMedicalHistory(@PathVariable String mabun) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String url = "https://studbook.kra.co.kr/html/info/ind/s_clinic_list.jsp?mabun=" + mabun;
        setSSL();
        Document doc = Jsoup.connect(url).get();

        Elements rows = doc.select(".boardList1 tbody tr");
        String latestDate = "";
        String latestDisease = "";

        if(rows.select("td").get(0).text().contains("없습니다")){
            latestDate = "-";
            latestDisease = "-";
        }else{
            for (Element row : rows) {
                String date = row.select("td").get(0).text();
                String disease = row.select("td").get(1).text();

                if (disease.contains("정치") && date.compareTo(latestDate) > 0) {
                    latestDate = date;
                    latestDisease = disease;
                }
            }
        }

        Map<String, String> medicalData = new HashMap<>();
        medicalData.put("mabun", mabun);
        medicalData.put("latest_date", latestDate);
        medicalData.put("latest_disease", latestDisease);

        return medicalData;
    }

    public static void setSSL() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManagerImpl() };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifierImpl());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    private static class X509TrustManagerImpl implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }

    private static class HostnameVerifierImpl implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    @GetMapping("/excel")
    public void downloadExcel(HttpServletResponse response, @RequestParam("jo") String jo) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        // Call the getTrainerAndHorseList method to get the horse and trainer data
        Map<String, Object> horseAndTrainerData = getTrainerAndHorseList(jo);
        if (horseAndTrainerData.containsKey("error")) {
            // Handle error if there is one
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, (String) horseAndTrainerData.get("error"));
            return;
        }

        // Get the horse list data from the horseAndTrainerData
        List<Map<String, Object>> horseList = (List<Map<String, Object>>) horseAndTrainerData.get("horse_list");

        // Create a workbook object
        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a sheet
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        // Create a header row
        XSSFRow headerRow = sheet.createRow(0);
        String[] headers = {"mabun", "name", "latest_date"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Create data rows
        int rowNum = 1;
        for (Map<String, Object> row : horseList) {
            XSSFRow dataRow = sheet.createRow(rowNum++);
            int cellNum = 0;
            for (String key : headers) {
                dataRow.createCell(cellNum++).setCellValue(row.get(key).toString());
            }
        }

        // Set the response headers
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String filename = date + "_" + jo +"_정치리스트.xlsx";
        String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFilename + "\"");

        // Write the workbook to the response stream
        workbook.write(response.getOutputStream());

        // Close the workbook
        workbook.close();


    }
}