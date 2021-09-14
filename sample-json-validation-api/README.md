# Red Hat Fuse implementation of the _Sample JSON Validation API_

## API Description ##
Sample JSON Validation API implemented by following a contract-first approach.

## Build ##

**Prerequisite**: 
- Make sure [fuse-common-resources](../fuse-common-resources/README.md) module jar has already been built and installed in your local maven repository

**Run the following command to build the project using JDK 11**:

```zsh
mvn clean package -Pjava11 -Djkube.skip
```

## Run locally ##

```zsh
mvn spring-boot:run -Pjava11 -Djkube.skip
```

## Deploy on OpenShift ##

**Prerequisite**: 
- A fully functional *OpenShift cluster* containing *Red Hat Fuse 7.9 imagestreams* is available and running.
- Please see the [Red Hat Fuse 7.9 on OpenShift Getting Started for Adminstrators](https://access.redhat.com/documentation/en-us/red_hat_fuse/7.9/html-single/fuse_on_openshift_guide/index#get-started-admin) for more details.

**Instructions**:
1. Update the [src/main/jkube/route.yml](src/main/jkube/route.yml) descriptor and adapt the route `host` according to your *OpenShift cluster*.
2. Run the [*Eclipse Jkube OpenShift Maven Plugin*](https://www.eclipse.org/jkube/docs/openshift-maven-plugin) goal to deploy the Red Hat Fuse application onto your *OpenShift cluster*.

```zsh
mvn clean oc:deploy -Pjava11
```

## Test ##

#### Locally ####

- Retrieve the OpenAPI specification: `curl -H 'Accept: application/json' http://localhost:8080/openapi.json`

```zsh
$ curl -H 'Accept: application/json' http://localhost:8080/openapi.json
```
```json
{
    "openapi": "3.0.2",
    "info": {
        "title": "Sample JSON Validation API",
        "version": "1.0.0",
        "description": "A simple API to test the Camel json-schema-validator component",
        "contact": {
            "name": "Jean Nyilimbibi"
        },
        "license": {
            "name": "MIT License",
            "url": "https://opensource.org/licenses/MIT"
        }
    },
    "servers": [
        {
            "url": "http://sample-json-validation-api.apps.jeannyil.sandbox706.opentlc.com",
            "description": "API Backend URL"
        }
    ],
    "paths": {
        "/validateMembershipJSON": {
            "post": {
                "requestBody": {
                    "description": "A `Membership` JSON instance to be validated.",
                    "content": {
                        "application/json": {
                            "schema": {
                                "$ref": "#/components/schemas/Membership"
                            }
                        }
                    },
                    "required": true
                },
                "responses": {
                    "200": {
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/ValidationResult"
                                },
                                "examples": {
                                    "validationResult_200": {
                                        "value": {
                                            "validationResult": {
                                                "status": "OK"
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        "description": "`Membership`JSON data validated"
                    },
                    "400": {
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/ValidationResult"
                                },
                                "examples": {
                                    "validationResult_400": {
                                        "value": {
                                            "validationResult": {
                                                "status": "KO",
                                                "errorMessage": "JSon validation error with 2 errors. Exchange[ID-sample-json-validation-api-1-nxgnq-1620389968195-0-427]"
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        "description": "`Membership`JSON data not valid"
                    },
                    "500": {
                        "content": {
                            "application/json": {
                                "schema": {
                                    "$ref": "#/components/schemas/Error"
                                },
                                "examples": {
                                    "error_500": {
                                        "value": {
                                            "error": {
                                                "id": "500",
                                                "description": "Internal Server Error",
                                                "messages": [
                                                    "java.lang.Exception: Mocked error message"
                                                ]
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        "description": "Internal server error"
                    }
                },
                "operationId": "validateMembershipJSON",
                "summary": "Validate Membership JSON instance",
                "description": "Validates a `Membership` JSON instance",
                "x-codegen-request-body-name": "body"
            }
        }
    },
    "components": {
        "schemas": {
            "Membership": {
                "description": "Membership data ",
                "required": [
                    "changedBy",
                    "endDate",
                    "enrolmentDate",
                    "memberID",
                    "requestID",
                    "requestType",
                    "vipOnInvitation"
                ],
                "type": "object",
                "properties": {
                    "requestType": {
                        "type": "string"
                    },
                    "requestID": {
                        "format": "int32",
                        "type": "integer"
                    },
                    "memberID": {
                        "format": "int32",
                        "type": "integer"
                    },
                    "status": {
                        "maxLength": 1,
                        "minLength": 1,
                        "enum": [
                            "A",
                            "B",
                            "C"
                        ],
                        "type": "string"
                    },
                    "enrolmentDate": {
                        "format": "date",
                        "type": "string"
                    },
                    "changedBy": {
                        "type": "string"
                    },
                    "forcedLevelCode": {
                        "type": "string"
                    },
                    "vipOnInvitation": {
                        "maxLength": 1,
                        "minLength": 1,
                        "enum": [
                            "N",
                            "Y"
                        ],
                        "type": "string"
                    },
                    "startDate": {
                        "format": "date",
                        "type": "string"
                    },
                    "endDate": {
                        "format": "date",
                        "type": "string"
                    }
                },
                "example": {
                    "requestType": "API",
                    "requestID": 5948,
                    "memberID": 85623617,
                    "status": "A",
                    "enrolmentDate": "2019-06-16",
                    "changedBy": "jeanNyil",
                    "forcedLevelCode": "69",
                    "vipOnInvitation": "Y",
                    "startDate": "2019-06-16",
                    "endDate": "2100-06-16"
                }
            },
            "ValidationResult": {
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
                "example": "{\n    \"validationResult\": {\n        \"status\": \"KO\",\n        \"errorMessage\": \"6 errors found\"\n    }\n}"
            },
            "Error": {
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
                                "items": {
                                    "type": "object"
                                }
                            }
                        }
                    }
                },
                "example": {
                    "error": {
                        "id": "500",
                        "description": "Internal Server Error",
                        "messages": [
                            "java.lang.Exception: Mocked error message"
                        ]
                    }
                }
            }
        }
    }
}
```

- Using *[Postman](https://www.getpostman.com/products)*
  - You may import the embedded [tests Postman collection](./tests/Sample_JSON_Validation_API.postman_collection.json)
  - Screenshots samples of validation tests using *[Postman](https://www.getpostman.com/products)*
![validateOKMembership.png](images/validateOKMembershipJSON.png)
![validateKOMembership.png](images/validateKOMembershipJSON.png)

#### OpenShift ####

Same instructions as above but replace the `localhost:8080` with your *OpenShift route for the service*.

For example, `http://sample-json-validation-api.apps.jeannyil.sandbox706.opentlc.com/openapi.json` will return the OpenAPI specification of the service.