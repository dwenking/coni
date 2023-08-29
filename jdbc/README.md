# How to run
1. Check project configurations in `GlobalConfiguration`
2. Start DB service
3. Run `Server`

# Support more features
If you want to support more features, check `fuzzer.Dict`, `connector.conn.Conn` and `fuzzer.arg.ArgFactory`. 

# Design Doc

## Instrumentation
目前使用Jacoco Agent插桩，需要依赖/jar下的jacocoagent.jar。覆盖率信息使用6300端口发送。