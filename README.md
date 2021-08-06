****Black Box Tester****

Build a runnable jar

`mvn clean install`

To run a Test Suite

`java -jar black-box-tester-1.0.jar testInput.json`

where testInput.json looks like:
```json
{
  "verbose": "true",
  "repeat": 1,
  "threads": 1,
  "httpTests": [
    {
      "description": "test 1",
      "url": "https://gorest.co.in/public/v1/users",
      "method": "GET",
      "headers": [
        {
          "name": "header1",
          "value": "value1"
        }
      ],
      "expected": {
        "httpStatus": 200,
        "contains": [
          "Atmaja Varrier",
          "Lakshminath Butt"
        ]
      }
    }
  ]
}
```