#!/usr/bin/env bash
# page 388 profile.sh
# 쉬고 있는 profile 찾기 : real1이 사용중이면 real2가 쉬고있고, 반대면 real1이 쉬고있음

function find_idle_profile() {
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile) # 1

    if [ ${RESPONSE_CODE} -ge 400 ] # 400 보다 크면 (즉, 40x/50x 에러 모두 포함)
    then
      CURRENT_PROFILE=prod2
    else
      CURRENT_PROFILE=$(curl -s http://localhost/profile)
    fi

    if [ ${CURRENT_PROFILE} == prod1 ]
    then
      IDLE_PROFILE=prod2 # 2
    else
      IDLE_PROFILE=prod1
    fi

    echo "${IDLE_PROFILE}" # 3
}

# 쉬고있는 profile의 port 찾기
function find_idle_port() {
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == prod1 ]
    then
      echo "8081"
    else
      echo "8082"
    fi
}