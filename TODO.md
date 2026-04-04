# TODO: Fix POST /api/auth/login 405 Error to Dashboard

## Status: In Progress

### Steps from Approved Plan:

1. **Verify Current Spring Mappings** 
   - Add actuator dependency to pom.xml if missing
   - Run app, check http://localhost:8080/actuator/mappings
   
2. **Update SecurityConfig.java** 
   - Add explicit `HttpMethod.POST, \"/api/auth/login\"` permitAll
   
3. **Update AuthController.java** 
   - Add logging for request received, auth attempt
   
4. **Update DashboardController.java** 
   - Temporarily comment @PreAuthorize for testing
   
5. **Test with curl** 
   - `curl -X POST -d \"username=admin&password=yourpass\" http://localhost:8080/api/auth/login -v`
   
6. **Restart and Test Browser Login** 
   - mvn spring-boot:run
   - Login from index.html
   
7. **Verify JWT Cookie & Dashboard** 
   - Check Network tab for cookie, dashboard loads
   
8. **Cleanup & Complete** 
   - Re-enable auth if needed
   - Mark complete
