Send OpenDDS JSON Message
---
- [Ref site](#ref-site)
- [OpenDDS Configure](#opendds-configure)
  - [include RapidJSON](#include-rapidjson)
- [USING OpenDDS JSON Encoder/Decoder Function](#using-opendds-json-encoderdecoder-function)
- [User JSON Encoder/Decoder](#user-json-encoderdecoder)
  - [Source Code](#source-code)


# Ref site
[1](https://github.com/OpenDDS/OpenDDS/discussions/4247)
[2](https://github.com/OpenDDS/OpenDDS/discussions/4559#discussioncomment-9011116)

# OpenDDS Configure
```bash
udo ./configure --verbose --java --doc-group3 --rapidjson --features=java_pre_jpms=0
```
## include [RapidJSON](https://rapidjson.org/) 
* A fast JSON parser/generator for C++ with both SAX/DOM style API
* RapidJSON is a JSON parser and generator for C++
* Features
  * RapidJSON is small but complete.
  * RapidJSON is fast.
  * RapidJSON is self-contained and header-only.
  * RapidJSON is Unicode-Frendly.
  * [More features](https://rapidjson.org/md_doc_features.html)

* when OpenDDS Configure, Need **RapidJSON**
  * RapidJSON을 깃헙에서 받아서 사용
  * $DDS_ROOT/tools/rapidjson 경로에 받은 파일 적용 후 configure
  
# USING OpenDDS JSON Encoder/Decoder Function
  * JSON Encoding and Decoding Using TypeSupport
  * $DDS_ROOT/java/tests/vread_vwrite/VreadVwrite.java <-- 사용 방법 및 함수있음
  * Function
  ```java
  // 예제에서는 Mod 라이브러리에 포함되어있는 SampleTypeSupport 사용, 실사용 할때는 ~~~TypeSupport 클래스 불러와서 사용하면됨.
  SampleTypeSupport ts = new SampleTypeSupportImpl(); // 불러온 JSON 데이터를 IDL에 매핑해주는 클래스
  RepresentationFormat format = ts.make_format(JSON_DATA_REPRESENTATION.value); // IDL의 포맷을 불러옴

  // Decode the JSON string into a sample and check for success.
  SampleHolder sample = new SampleHolder(); // 실 사용시 ~~~Holder 클래스 사용
  int ret = ts.decode_from_string(json, sample, format); // 불러온 JSON을 idl(sample)에 format에 맞게 매핑해줌 성공시 0 실패시 1
  // idl과 json의 매핑구조가 다르면 error 발생

  // Encode the sample as a JSON string.
  StringHolder holder = new StringHolder(); //JSON 데이터를 encoding하여 넣어주기 위한 holder 클래스
  // 이 클래스는 import org.omg.CORBA.StringHolder; 선언하여 사용
  ret = ts.encode_to_string(sample.value, holder, format); // JSON 값(sample.value)를 holder 클래스에 넣어줌 성공시 0 실패시 1

  // Nomalize the sample from the file and compare.
  String nomalized = json.replaceALL("\\s", ""); // 주어진 JSON 문자열에서 모든 공백 문자(스페이스, 탭, 줄 바꿈 등)을 제거
  if (!normalized.equals(holder.value)) {
        System.out.println("ERROR: round-trip JSON doesn't match: " + holder.value);
        System.exit(1);
        }
  ```
  * 결국 IDL과 JSON의 구조가 같아야지만 사용할 수 있는 encoder_decoder..
  * 구현 목표인 IDL을 재빌드 하지 않고, JSON 파싱과는 거리가 멈..

# User JSON Encoder/Decoder
* OpenDDS에서 제공해주는 클래스로는 적합 하지 않아 JSON Library를 사용하여 직접 구현
* [JSON Library jar Path](https://www.json.org/json-en.html)
  * 이곳에서 jar파일을 받아 import
* JSON 파일을 불러온 후 message 객체에 불러온 json 그대로 넣어주면 JSON 형식으로 Subscriber에 전송해줌
* 원하는 값만 출력하거나 DB 저장을 편하게 하기 위해...
  * Subscriber에서 받은 JSON을 DDS Encoder 클래스를 사용하여 String 형식으로 변경
   ```java
    // Load Class
    PinJsonTypeSupport ts = new PinJsonTypeSupportImpl();
    RepresentationFormat format =ts.make_format(JSON_DATA_REPRESENTATION.value);
    StringHolder holder = new StringHolder();
    
    //JSON Encoder
    ts.encode_to_string(mh.value, holder, format);
    // System.out.println(holder.value); <-- mh.value 안에 있는 JSON 데이터를 holder안에 넣어준 후 String으로 인코딩

    JSONObject jObject = new JSONObject(holder.value); // String의 값을 JSON Object로 변경
    String bodyString = jObject.getString("body"); // key 값이 body인 값 String으로 추출(찐 데이터)
    JSONObject body = new JSONObject(bodyString);  // 추출한 body 값 Json object로 변경
    JSONObject io = body.getJSONObject("IdentifiedObject"); // body에서 각 키 값에 따른 value 값 가져옴
    JSONObject length = body.getJSONObject("Length");

    String description = io.getString("description"); // value 값에서 key에 대한 값 string으로 가져옴
    String mRID = io.getString("mRID");
    String name = io.getString("name");
    String aliasName = io.getString("aliasName");

    String unit = length.getString("unit");
    String multiplier = length.getString("multiplier");
    double value = length.getDouble("value");

    System.out.println("\n======IdentifiedObject Value====== "); // 출력
    System.out.println(description);
    System.out.println(mRID);
    System.out.println(name);
    System.out.println(aliasName);

    System.out.println("\n\n=======Length Value=======");
    System.out.println(unit);
    System.out.println(multiplier);
    System.out.println(value);
   ```
## Source Code
* User JSON Encoder/Decoder 소스코드는 폴더 내 첨부