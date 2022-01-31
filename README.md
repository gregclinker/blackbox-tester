****API Black Box Tester****

Build a runnable jar

`mvn clean install`

To run a Test Suite

`java -jar black-box-tester-1.0.jar testInput.json`

where testInput.json looks like:

```yaml
---
verbose: true
repeat: 1
threads: 1
timeout: 20
httpTests:
  - description: Get Users
    url: https://gorest.co.in/public/v1/users
    method: GET
    headers:
      - name: Accept
        value: application/json
      - name: Content-Type
        value: application/json
    expected:
      httpStatus: 200
      contains:
        - TONY STARK
        - Vaidehi Bhat
  - description: Post Users
    url: https://gorest.co.in/public/v1/users
    method: POST
    headers:
      - name: Accept
        value: application/json
      - name: Content-Type
        value: application/json
      - name: Authorization
        value: Bearer ACCESS-TOKEN
    body: '{"name":"Tenali Ramakrishna","gender":"male","email":"tenali.ramakrishna@15ce.com","status":"active"}'
    expected:
      httpStatus: 401
```
