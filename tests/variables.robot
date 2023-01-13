*** Variables ***
${uri}=    http://192.168.178.108:8000

${traceparentA}=    traceparentA
${traceparentB}=    traceparentB
${traceparentC}=    traceparentC

${essenceShort}=      1,1;2,1;
${essence}=           1,1;2,1;3,1;4,1;5,1;6,1;7,1;8,1;9,1;
${essenceInvalid}=    mockedinvalidessence

${hiveEssenceHeader}=    application/hive-essence
${contentHeader}=        text/plain

${contentA}=    contentA
${contentB}=    contentB

${clearAll}=    all