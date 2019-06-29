# Red Hat Fuse implementation of the _Sample XML Validation API_

## API Description ##
Sample XML Validation API implemented by following a contract-first approach.

## Build ##

**Prerequisite**: 
- Make sure [fuse-common-resources](../fuse-common-resources/README.md) module jar has already been built and installed in your local maven repository

**Run the following command to build the project**:

```
mvn clean package -Dfabric8.skip
```

## Run locally ##

```
mvn clean spring-boot:run -Dfabric8.skip=true
```

## Deploy on OpenShift ##

**Prerequisite**: 
- A fully functional *OpenShift cluster* containing *Red Hat Fuse 7.3 imagestreams* is available and running.
- Please see the [Red Hat Fuse 7.3 on OpenShift Getting Started for Adminstrators](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.3/html-single/fuse_on_openshift_guide/index#get-started-admin) for more details.

**Instructions**:
1. Update the [src/main/fabric8/route.yml](src/main/fabric8/route.yml) descriptor and adapt the route `host` according to your *OpenShift cluster*.
2. Run the *Fabric8 Maven Plugin* goal to deploy the Red Hat Fuse application onto your *OpenShift cluster*.

```
mvn clean fabric8:deploy
```

## Test ##

#### Locally ####

- Retrieve the OpenAPI document: `curl http://localhost:8080/openapi.json`

```
$ curl http://localhost:8080/openapi.json
{
    "swagger": "2.0",
    "info": {
        "title": "Sample XML Validation API",
        "description": "A simple API to test the Camel XML validator component",
        "contact": {
            "name": "Jean Nyilimbibi"
        },
        "license": {
            "name": "MIT License",
            "url": "https://opensource.org/licenses/MIT"
        },
        "version": "1.0.0"
    },
    "consumes": [
        "text/xml"
    ],
    "produces": [
        "application/json"
    ],
    "paths": {
        "/validateMembershipXML": {
            "post": {
                "summary": "Validate Membership XML instance",
                "description": "Validates a `Membership` instance",
                "operationId": "validateMembershipXML",
                "consumes": [
                    "text/xml"
                ],
                "parameters": [
                    {
                        "name": "body",
                        "in": "body",
                        "description": "A `Membership` XML instance to be validated.",
                        "required": true,
                        "schema": {
                            "type": "string"
                        }
                    }
                ],
                "responses": {
                    "200": {
                        "description": "`Membership` XML data validated",
                        "schema": {
                            "$ref": "#/definitions/ValidationResult"
                        }
                    },
                    "400": {
                        "description": "`Membership` XML data not valid",
                        "schema": {
                            "$ref": "#/definitions/ValidationResult"
                        }
                    },
                    "500": {
                        "description": "Internal server error",
                        "schema": {
                            "$ref": "#/definitions/Error"
                        }
                    }
                }
            }
        }
    },
    "definitions": {
        "ValidationResult": {
            "title": "Root Type for ValidationResult",
            "description": "Validation Result   ",
            "type": "object",
            "properties": {
                "validationResult": {
                    "type": "object",
                    "properties": {
                        "status": {
                            "maxLength": 2,
                            "minLength": 2,
                            "enum": [
                                "OK",
                                "KO"
                            ],
                            "type": "string"
                        },
                        "errorMessage": {
                            "type": "string"
                        }
                    }
                }
            },
            "example": "{\n\t\"validationResult\": {\n\t\t\"status\": \"KO\",\n\t\t\"errorMessage\": \"Validation failed for: com.sun.org.apache.xerces.internal.jaxp.validation.SimpleXMLSchema@5f86796e\\nerrors: [\\norg.xml.sax.SAXParseException: cvc-datatype-valid.1.2.1: '20-06-29' is not a valid value for 'date'., Line : 7, Column : 46\\norg.xml.sax.SAXParseException: cvc-type.3.1.3: The value '20-06-29' of element 'p:enrolmentDate' is not valid., Line : 7, Column : 46\\n]. Exchange[ID-jeansmacbookair-home-1561803539861-1-1]\"\n\t}\n}"
        },
        "Error": {
            "title": "Root Type for Error",
            "description": "Error message structure",
            "type": "object",
            "properties": {
                "error": {
                    "type": "object",
                    "properties": {
                        "id": {
                            "type": "string"
                        },
                        "description": {
                            "type": "string"
                        },
                        "messages": {
                            "type": "array",
                            "items": {}
                        }
                    }
                }
            },
            "example": "{\n\t\"error\": {\n\t\t\"id\": \"500\",\n\t\t\"description\": \"Internal Server Error\",\n\t\t\"messages\": [\n\t\t\t\"java.lang.Exception: Mocked error message\"\n\t\t]\n\t}\n}"
        }
    },
    "tags": [
        {
            "name": "RESTDSL"
        },
        {
            "name": "fuse7springboot"
        },
        {
            "name": "xmlvalidator"
        }
    ]
}
```

- Using *[Postman](https://www.getpostman.com/products)*
  - You may import the embedded [tests Postman collection](./tests/Sample_XML_Validation_API.postman_collection.json)
  - Screenshots samples of validation tests using *[Postman](https://www.getpostman.com/products)*
![validateOKMembershipXML.png](images/validateOKMembershipXML.png)
![validateKOMembershipXML.png](images/validateKOMembershipXML.png)

#### OpenShift ####

Same instructions as above but replace the `localhost:8080` with your *OpenShift route for the service*.

For example, `http://sample-xml-validation-api.apps.69ac.example.opentlc.com/openapi.json` will return the OpenAPI document used to implement the service.