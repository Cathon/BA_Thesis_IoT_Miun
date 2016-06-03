# BA_Thesis_IoT_Miun
Source code of programs in the thesis

## Code of own developed programs

### source node of SensibleThings in scenario one
see file `STsource_1.java`

### sink node SensibleThings in scenario one
see file `STsink_1.java`

### source node of SensibleThings in scenario two
see file `STsource_2.java`

### sink node SensibleThings in scenario two
see file `STsink_2.java`

### local bootstrap of SensibleThings
See file `STbootstrap.java`

---
### event scheme of Kaa in scenario one
```
EC: event class
FQN: fully qualified name (com.company.project.TestEvent)
ECF: event class families

// event name family
Name: IoT Event Class Family
Namespace: org.kaaproject.kaa.demo.iot
Class name: IoTEventClassFamily

// event class schema
{
  "namespace": "com.company.project",
  "type": "record",
  "classType": "event",
  "name": "TestEvent",
  "fields": [
    { "name": "timestamp", "type": "Long"}
  ]
}
```

### endpoint A of Kaa in scenario one
See file `Kaasource.java`

### endpoint B of Kaa in scenario one
See file `Kaasink.java`

### log scheme of Kaa in scenario two
```
{
  "type" : "record",
  "name" : "IoT_Test_For_S2",
  "namespace" : "com.company.project",
  "fields" : [ {
    "name" : "NodeID",
    "type" : {
      "type" : "string",
      "avro.java.string" : "String"
    }
  }, {
    "name" : "MsgID",
    "type" : "int"
  }, {
    "name" : "timestamp",
    "type" : "long"
  } ]
}
```

### endpoints of Kaa in scenario two
See file `Kaacollect.java`


