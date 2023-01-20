*** Settings ***
Documentation    Test suite for Hivemind
Resource         variables.robot
Library          RequestsLibrary
Library          Collections
Library          String

Suite Setup    Create Session    Hivemind    ${uri}

*** Test Cases ***
Clear Before - All tests
    Clean

Synchronizing data from A to B
    [Documentation]    A having data, B having no data, will synchronize data to B
    [Tags]             A-B
    ${headers}=        Create Dictionary                                              traceparent=${traceparentA}    content-type=${hiveEssenceHeader}
    ${resp}=           POST On Session                                                Hivemind                       /                                    data=${essenceShort}    headers=${headers}

    Status Should Be    NO_CONTENT    ${resp}

    ${headers}=    Create Dictionary    traceparent=${traceparentB}    content-type=${hiveEssenceHeader}
    ${resp}=       POST On Session      Hivemind                       /                                    headers=${headers}

    Status Should Be    NO_CONTENT    ${resp}

    ${headers}=    Create Dictionary    traceparent=${traceparentA}    content-type=${hiveEssenceHeader}
    ${resp}=       POST On Session      Hivemind                       /                                    data=${essenceShort}    headers=${headers}

    Status Should Be    OK              ${resp}
    Should Be Equal     ${resp.text}    ${essenceShort}

    ${headers}=    Create Dictionary    traceparent=${traceparentA}    content-type=${contentHeader}
    ${resp}=       POST On Session      Hivemind                       /                                data=${contentA}    headers=${headers}

    Status Should Be    NO_CONTENT    ${resp}

    ${headers}=    Create Dictionary    traceparent=${traceparentB}    content-type=${hiveEssenceHeader}
    ${resp}=       POST On Session      Hivemind                       /                                    headers=${headers}

    Status Should Be    OK              ${resp}
    Should Be Equal     ${resp.text}    ${contentA}

Clear After - Synchronizing data from A to B
    Clean

Synchronizing data from A but not a valid essence
    [Documentation]    A sending synchronize request but with an invalid essence
    [Tags]             A essence invalid
    ${headers}=        Create Dictionary                                            traceparent=${traceparentA}    content-type=${hiveEssenceHeader}
    ${resp}=           Run Keyword And Ignore Error                                 POST On Session                Hivemind                             /    data=${essenceInvalid}    headers=${headers}

    Should Contain    ${resp[1]}    HTTPError: 400 Client Error

Synchronizing data from A to B with B performing priority update for A
    [Documentation]    A having data, B having data, B will synchronize priority request data to A
    [Tags]             B-A B priority
    ${headers}=        Create Dictionary                                                              traceparent=${traceparentA}    content-type=${hiveEssenceHeader}
    ${resp}=           POST On Session                                                                Hivemind                       /                                    data=${essence}    headers=${headers}

    Status Should Be    NO_CONTENT    ${resp}

    ${headers}=    Create Dictionary    traceparent=${traceparentB}    content-type=${hiveEssenceHeader}
    ${resp}=       POST On Session      Hivemind                       /                                    data=${essence}    headers=${headers}

    Status Should Be    NO_CONTENT    ${resp}

    ${headers}=    Create Dictionary    traceparent=${traceparentB}    content-type=${hiveEssenceHeader}
    ${resp}=       POST On Session      Hivemind                       /                                    data=${essenceShort}    headers=${headers}

    Status Should Be    NO_CONTENT    ${resp}

    ${headers}=    Create Dictionary               traceparent=${traceparentA}    content-type=${hiveEssenceHeader}
    ${resp}=       Run Keyword And Ignore Error    POST On Session                Hivemind                             /    data=${essence}    headers=${headers}

    Should Contain    ${resp[1]}    HTTPError: 409 Client Error

Clear After - Synchronizing data from A to B with B performing priority update for A
    Clean

*** Keywords ***
Clean
    Log To Console    clearing
    ${resp}=          POST On Session    Hivemind    /manager    data=${clearAll}
