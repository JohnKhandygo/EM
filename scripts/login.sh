#!/bin/bash

curl -k -X POST \
	--header 'Content-Type: application/x-www-form-urlencoded' \
	--header 'Accept: application/json' \
	-d 'login=deve' \
	-d 'password=1234' \
	'https://localhost:8443/api/auth'