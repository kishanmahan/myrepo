package org.SailPoint_IIQ.Utils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import sailpoint.api.SailPointContext;
import sailpoint.object.ProvisioningPlan;
import sailpoint.object.ProvisioningPlan.AccountRequest;
import sailpoint.object.ProvisioningPlan.AccountRequest.Operation;
import sailpoint.object.ProvisioningPlan.AttributeRequest;
import sailpoint.object.ProvisioningResult;
import sailpoint.tools.GeneralException;

public class FinancialAndForecastingProvisioningRule {
	static PreparedStatement stmt = null;
	static String defaultRoleId = "ROL_FIN_AUDIT_VIEWER_010";
	static String ApplicationName = "Financial Planning & Forecasting";
    static Logger logs = Logger.getLogger("org.SailPoint_IIQ.Utils.FinancialAndForecastingProvisioningRule");
	public static ProvisioningResult doProvisioning(ProvisioningPlan plan, Connection connection,
			SailPointContext context) throws SQLException, GeneralException {

		System.out.println("doProvisioning method called;;;;;plan" + plan.toXml());
		logs.debug("FinancialAndForecastingProvisioningRule method called");
		ProvisioningResult result = new ProvisioningResult();
		// PreparedStatement stmt = null;
		AccountRequest acctReq = null;

		List<AccountRequest> acctReqs = plan.getAccountRequests(ApplicationName);
		for (AccountRequest accountRequest : acctReqs) {
			acctReq = accountRequest;
			break;
		}
		if (acctReq == null) {
			logs.error("No account request found for "+ApplicationName);
			return result;   //yaha ke baad ish method ka code nahi chalega.
		}
		Operation op = acctReq.getOperation();
		if (AccountRequest.Operation.Create.equals(op)) {
			result = createAccount(plan, connection, acctReq);
		} else if (AccountRequest.Operation.Disable.equals(op)) {
			String employeeId = acctReq.getNativeIdentity();
			result = disableAccount(connection, employeeId);
		} else if (AccountRequest.Operation.Modify.equals(op)) {
			 result =modifyAccount(plan,connection,acctReq);
		}else if (AccountRequest.Operation.Enable.equals(op)) {
			 result =enableAccount(plan,connection,acctReq);
		}

		return result;

	}

	


	private static ProvisioningResult createAccount(ProvisioningPlan plan, Connection connection, AccountRequest acctReq) {
		//System.out.println("createAccount method called ");
		logs.debug("createAccount method called ");
		ProvisioningResult result = new ProvisioningResult();
		List<AttributeRequest> attributeRequests = acctReq.getAttributeRequests();
		String employeeId = acctReq.getNativeIdentity();
		String employee = "", firstName = "", lastName = "", empStatus = "", department = "", userLevel = "",
				hireDate = "", salaryClass = "", annualHours = "", jobTitle = "";
		String terminationDate = "", workState = "", email = "", countryCode = "", workCountry = "", manager = "",
				superviserId = "", bussinessUnit = "", isManager = "";

		AttributeRequest attributeRequestEmployee = acctReq.getAttributeRequest("employee");
		if (attributeRequestEmployee != null)
			employee = attributeRequestEmployee.getValue().toString();

		AttributeRequest attributeRequestFirstName = acctReq.getAttributeRequest("firstName");
		if (attributeRequestFirstName != null)
			firstName = attributeRequestFirstName.getValue().toString();

		AttributeRequest attributeRequestLastName = acctReq.getAttributeRequest("lastName");
		if (attributeRequestLastName != null)
			lastName = attributeRequestLastName.getValue().toString();

		AttributeRequest attributeRequestEmpStatus = acctReq.getAttributeRequest("empStatus");
		if (attributeRequestEmpStatus != null)
			empStatus = attributeRequestEmpStatus.getValue().toString();

		AttributeRequest attributeRequestDepartment = acctReq.getAttributeRequest("department");
		if (attributeRequestDepartment != null)
			department = attributeRequestDepartment.getValue().toString();

		AttributeRequest attributeRequestUserLevel = acctReq.getAttributeRequest("userLevel");
		if (attributeRequestUserLevel != null)
			userLevel = attributeRequestUserLevel.getValue().toString();

		AttributeRequest attributeRequestHireDate = acctReq.getAttributeRequest("hireDate");
		if (attributeRequestHireDate != null)
			hireDate = attributeRequestHireDate.getValue().toString();

		AttributeRequest attributeRequestSalaryClass = acctReq.getAttributeRequest("salaryClass");
		if (attributeRequestSalaryClass != null)
			salaryClass = attributeRequestSalaryClass.getValue().toString();

		AttributeRequest attributeRequestTerminationDate = acctReq.getAttributeRequest("terminationDate");
		if (attributeRequestTerminationDate != null)
			terminationDate = attributeRequestTerminationDate.getValue().toString();

		AttributeRequest attributeRequestWorkState = acctReq.getAttributeRequest("workState");
		if (attributeRequestWorkState != null)
			workState = attributeRequestWorkState.getValue().toString();

		AttributeRequest attributeRequestEmail = acctReq.getAttributeRequest("email");
		if (attributeRequestEmail != null)
			email = attributeRequestEmail.getValue().toString();

		AttributeRequest attributeRequestCountryCode = acctReq.getAttributeRequest("countryCode");
		if (attributeRequestCountryCode != null)
			countryCode = attributeRequestCountryCode.getValue().toString();

		AttributeRequest attributeRequestWorkCountry = acctReq.getAttributeRequest("workCountry");
		if (attributeRequestWorkCountry != null)
			workCountry = attributeRequestWorkCountry.getValue().toString();

		AttributeRequest attributeRequestManager = acctReq.getAttributeRequest("manager");
		if (attributeRequestManager != null)
			manager = attributeRequestManager.getValue().toString();

		AttributeRequest attributeRequestSuperviserId = acctReq.getAttributeRequest("superviserId");
		if (attributeRequestSuperviserId != null)
			superviserId = attributeRequestSuperviserId.getValue().toString();

		AttributeRequest attributeRequestAnnualHours = acctReq.getAttributeRequest("annualHours");
		if (attributeRequestAnnualHours != null)
			annualHours = attributeRequestAnnualHours.getValue().toString();

		AttributeRequest attributeRequestIsManager = acctReq.getAttributeRequest("isManager");
		if (attributeRequestIsManager != null)
			isManager = attributeRequestIsManager.getValue().toString();

		AttributeRequest attributeRequestJobTitlejobTitle = acctReq.getAttributeRequest("jobTitle");
		if (attributeRequestJobTitlejobTitle != null)
			jobTitle = attributeRequestJobTitlejobTitle.getValue().toString();

		String insertUserSQL = "INSERT INTO financial_planning_and_forecasting.employees "
				+ "(employeeId, employee, firstName, lastName, empStatus, department, userLevel, hireDate, "
				+ "salaryClass, terminationDate, workState, email, countryCode, workCountry, manager, "
				+ "superviserId, bussinessUnit, annualHours, isManager,jobTitle) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			stmt = connection.prepareStatement(insertUserSQL);
			stmt.setString(1, employeeId);
			stmt.setString(2, employee);
			stmt.setString(3, firstName);
			stmt.setString(4, lastName);
			stmt.setString(5, empStatus);
			stmt.setString(6, department);
			stmt.setString(7, userLevel);
			stmt.setString(8, hireDate);
			stmt.setString(9, salaryClass);
			stmt.setString(10, terminationDate);
			stmt.setString(11, workState);
			stmt.setString(12, email);
			stmt.setString(13, countryCode);
			stmt.setString(14, workCountry);
			stmt.setString(15, manager);
			stmt.setString(16, superviserId);
			stmt.setString(17, bussinessUnit);
			stmt.setString(18, annualHours);
			stmt.setString(19, isManager);
			stmt.setString(20, jobTitle);

			stmt.executeUpdate();
			stmt.close();
			//System.out.println("Employee table created");
			logs.debug("Employee table created ");
			for (AttributeRequest ar : attributeRequests) {
				if ("roles".equalsIgnoreCase(ar.getName()) && ("Add".equalsIgnoreCase(ar.getOperation().toString())
						|| "Set".equalsIgnoreCase(ar.getOperation().toString()))) {
					List<String> groupList = new LinkedList<String>();
					if ("roles".equalsIgnoreCase(ar.getName())) {
						if (ar.getValue() instanceof String) {
							groupList.add((String) ar.getValue());
						} else if (ar.getValue() instanceof List) {
							groupList = (List) ar.getValue();
						}
						logs.debug("groupList;;;;" + groupList);
						for (String roleName : groupList) {
							//System.out.println("Assigning roleName: " + roleName);
							logs.debug("Assigning roleName: " + roleName);
							String roleId = roleIdByName(connection, roleName);
							if (roleId != null) {
								//System.out.println("retriving roleId: " + roleId);
								logs.debug("retriving roleId: " + roleId);
								int updateStatus = addRole(connection, employeeId, roleId);
								if (updateStatus > 0) {
									logs.debug("role added ");
									result.setStatus(ProvisioningResult.STATUS_COMMITTED);
								}
							} else {
								logs.warn("Role not found in DB: " + roleName);
								//System.out.println("Role not found in DB: " + roleName);
								result.setStatus(ProvisioningResult.STATUS_FAILED);
							}
						}
					}
				}
			}
			// check and add default role
			 if (!checkExistingRole(connection, employeeId, defaultRoleId)) {
				logs.debug(" default role added ");
				int res = addRole(connection, employeeId, defaultRoleId);
				if(res>0)
				result.setStatus(ProvisioningResult.STATUS_COMMITTED);
				else
					result.setStatus(ProvisioningResult.STATUS_FAILED);
			}
		} catch (SQLException e) {
			logs.error("Exception during provisioning " , e);
			//System.out.println("Exception during provisioning " + e);
			result.setStatus(ProvisioningResult.STATUS_FAILED);
			result.addError(e);

		}
		return result;
	}//ProvisioningResult createAccount close 

	private static String roleIdByName(Connection connection, String name) throws SQLException {
		String getRoleIdSQL = "SELECT roleId FROM financial_planning_and_forecasting.roles WHERE name = ?";
		String roleId = null;
		stmt = connection.prepareStatement(getRoleIdSQL);
		stmt.setString(1, name);
		ResultSet rs = stmt.executeQuery();

		if (rs.next()) {
			roleId = rs.getString("roleId");
			rs.close();
			stmt.close();

		}
		return roleId;
	}

	private static int addRole(Connection connection, String employeeId, String roleId) throws SQLException {
		String insertMapSQL = "INSERT IGNORE INTO financial_planning_and_forecasting.employee_roles(employeeId,roleId) VALUES (?, ?)";
		stmt = connection.prepareStatement(insertMapSQL);
		stmt.setString(1, employeeId);
		stmt.setString(2, roleId);
		int executeStatus = stmt.executeUpdate();
		stmt.close();
		return executeStatus;
	}
	private static int removeRole(Connection connection, String employeeId, String roleId) throws SQLException {
		String insertMapSQL = "DELETE FROM financial_planning_and_forecasting.employee_roles WHERE employeeId = ? AND roleId = ?";
		stmt = connection.prepareStatement(insertMapSQL);
		stmt.setString(1, employeeId);
		stmt.setString(2, roleId);
		int executeStatus = stmt.executeUpdate();
		stmt.close();
		return executeStatus;
	}

	private static boolean checkExistingRole(Connection connection, String employeeId, String roleId)
			throws SQLException {
		ResultSet rs = null;
		boolean exists = false;
		String checkSQL = "SELECT 1 FROM financial_planning_and_forecasting.employee_roles WHERE employeeId = ? AND roleId = ?";
		try {
			stmt = connection.prepareStatement(checkSQL);
			stmt.setString(1, employeeId);
			stmt.setString(2, roleId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				// Record exists
				logs.debug("Role exist");
				exists = true;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

		return exists;
	}

	private static boolean isAnyRoleForEmployee(Connection connection, String employeeId)
			throws SQLException {
		ResultSet rs = null;
		boolean exists = false;
		String checkSQL = "SELECT 1 FROM financial_planning_and_forecasting.employee_roles WHERE employeeId = ?";
		try {
			stmt = connection.prepareStatement(checkSQL);
			stmt.setString(1, employeeId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				// Record exists
				logs.debug("Employee contains role");
				exists = true;
			}
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
		}

		return exists;
	}
	private static ProvisioningResult disableAccount(Connection connection, String  employeeId) throws SQLException {
		//System.out.println("disableAccount method called ");
		logs.debug("disableAccount method called");
		ProvisioningResult result = new ProvisioningResult();
		String empStatus = "Inactive";
		String updateQuery = "UPDATE financial_planning_and_forecasting.employees SET empStatus = ? WHERE employeeId = ?";
		stmt = connection.prepareStatement(updateQuery);
		stmt.setString(1, empStatus);
		stmt.setString(2, employeeId);
		int executeStatus = stmt.executeUpdate();
		stmt.close();
		if (executeStatus > 0) {
			//System.out.println("account disable success ");
			logs.debug("Account disable success");
			result.setStatus(ProvisioningResult.STATUS_COMMITTED);
		}else {
			result.setStatus(ProvisioningResult.STATUS_FAILED);
		}
		return result;

	}
	
	private static ProvisioningResult modifyAccount(ProvisioningPlan plan, Connection connection, AccountRequest acctReq) throws SQLException {
		//System.out.println("modifyAccount method called ");
		logs.debug("modifyAccount method called");
		ProvisioningResult result = new ProvisioningResult();
		result.setStatus(ProvisioningResult.STATUS_FAILED);
		List<AttributeRequest> attributeRequests = acctReq.getAttributeRequests();
		String employeeId = acctReq.getNativeIdentity();

		for (AttributeRequest ar : attributeRequests) {
			String roleId = null;
			List<String> groupList = new LinkedList();
			if ("roles".equalsIgnoreCase(ar.getName())) {
				if (ar.getValue() instanceof String) {
					groupList.add(ar.getValue().toString());
				} else if (ar.getValue() instanceof List) {
					groupList = (List) ar.getValue();
				}
				for (String roleName : groupList) {

					if ("Add".equals(ar.getOperation().toString())
							|| "Set".equalsIgnoreCase(ar.getOperation().toString())) {
						roleId = roleIdByName(connection, roleName);
						if (roleId != null) {
							int executeStatus =addRole(connection,employeeId,roleId);
							if (executeStatus > 0) {
								logs.debug(roleName+"-> Role added success ");
								//System.out.println("Role added success ");
								result.setStatus(ProvisioningResult.STATUS_COMMITTED);
							}
						}
						
					}else if("Remove".equals(ar.getOperation().toString())){
						roleId = roleIdByName(connection, roleName);
						int rm =removeRole(connection,employeeId,roleId);
						if(rm>0) {
							logs.debug(roleName+"-> Role remove success ");
                        result.setStatus(ProvisioningResult.STATUS_COMMITTED);
						}
					}
				}
			}
		}
        if(!isAnyRoleForEmployee(connection,employeeId)) {
        	result=disableAccount(connection,employeeId);
        	logs.debug(" There is no role so i done his account is disable ");
        }
		return result;

	}//modifyAccount close
	
	private static ProvisioningResult enableAccount(ProvisioningPlan plan, Connection connection,
			AccountRequest acctReq) throws SQLException {
		//System.out.println("disableAccount method called ");
		logs.debug("EnableOperation method called");
		ProvisioningResult result = new ProvisioningResult();
		String employeeId = acctReq.getNativeIdentity();
		String empStatus = "Active";
		String updateQuery = "UPDATE financial_planning_and_forecasting.employees SET empStatus = ? WHERE employeeId = ?";
		stmt = connection.prepareStatement(updateQuery);
		stmt.setString(1, empStatus);
		stmt.setString(2, employeeId);
		int executeStatus = stmt.executeUpdate();
		stmt.close();
		if (executeStatus > 0) {
			logs.debug("Account enable success");
			if (!checkExistingRole(connection, employeeId, defaultRoleId)) {
				logs.debug(" default role added ");
				int res = addRole(connection, employeeId, defaultRoleId);
				if(res>0)
				result.setStatus(ProvisioningResult.STATUS_COMMITTED);
				else
					result.setStatus(ProvisioningResult.STATUS_FAILED);
			}
		}else {
			result.setStatus(ProvisioningResult.STATUS_FAILED);
		}
		return result;

	}

	 
}
/*//////////
()doProvisioning() ko tum directly code me call nahi kar rahe ho. Isko SailPoint IIQ provisioning framework call karta hai.
Flow samjho:-
Identity Refresh
     ↓
LCM Request / Workflow / Role Assignment.
     ↓
ProvisioningPlan banta hai.
     ↓
Application Provisioning Rule execute hota hai.
     ↓
SailPoint tumhara doProvisioning() method call karta hai.

Agar tumne Application me Provisioning Rule configure kiya hai:-
<Application name="Financial Planning & Forecasting">
    <Attributes>
        <Map>
            <entry key="provisionRule"
                   value="FinancialAndForecastingProvisioningRule"/>
        </Map>
    </Attributes>
</Application>
ya JDBC connector configuration me.ha humne kiya hai ui se Add Application me.
--------------------------------------------------------------------------------
()for (AccountRequest accountRequest : acctReqs) {
			acctReq = accountRequest;
			break;
		} yaha sirf aek hi baar loop chala kyu :-
		
-->Is loop ko dekh kar lagta hai ki jab break lagaya hai toh loop chalane ka fayda hi kya hua?
-->Iska seedha sa jawab hai: Hamein poorre list mein se sirf PEHLA account request chahiye, baaki se koi matlab nahi hai.
()1. SailPoint ka Pattern (The Reason):-
SailPoint IIQ mein jab hum kisi single application (jaise: Financial Planning & Forecasting)
ke liye provisioning karte hain, toh 99% cases mein us plan ke andar us application ka sirf EK hi AccountRequest hota hai.

Lekin, SailPoint ka jo method hai:-
Java
plan.getAccountRequests(ApplicationName);
Ye hamesha data ko ek List (yaani collection) ke roop mein deta hai, bhale hi uske andar sirf 1 hi item kyu na ho. 
Java ke niyam ke mutabik, List se data nikaalne ke liye loop chalana padta hai.

--------------------------------------------------------------------------------
()1. "INSERT INTO financial_planning_and_forecasting.employees "
				+ "(employeeId, employee, firstName, lastName, empStatus, department, userLevel, hireDate, "
				+ "salaryClass, terminationDate, workState, email, countryCode, workCountry, manager, "
				+ "superviserId, bussinessUnit, annualHours, isManager,jobTitle) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";:-

-->Database Columns (Jo Query ke andar hain):-
SQL
(employeeId, employee, firstName, lastName, ...)
Yeh sirf ek plain text (String) hai jo database ko batata hai ki: "Bhai, 
mere database table mein yeh 20 columns hain, inke andar data daalna hai."
--------------------------------------------------------------------------------
()1."INSERT IGNORE INTO financial_planning_and_forecasting.employee_roles (employeeId,roleId) VALUES (?, ?)"; ye kya bol raha hai?
Yeh database ko bol raha hai ki financial_planning_and_forecasting database ke andar jo employee_roles naam ki table hai, 
usme ek naya record (row) daal do. Is table mein do columns hain: employeeId aur roleId.
2. Yeh IGNORE kya kar raha hai? 
Yahan par IGNORE keyword sabse kaam ki cheez hai. Iska matlab hai: "Agar yeh role is employee ko pehle se mila hua hai, 
toh dobara mat daalna aur koi error bhi mat dena."

-->executeStatus mein 1 aaya ($> 0$)इसका मतलब है कि Role successfully assign ho gaya. agar 0 aata toh kuch nahi badla.
-------------------------------------------------------
()"UPDATE financial_planning_and_forecasting.employees SET empStatus = ? WHERE employeeId = ?"; kya bol raha hai:-
1. UPDATE financial_planning_and_forecasting.employees
Yeh database ko batata hai ki hume employees naam ki table ke andar pehle se maujood kisi record (row) mein badlav (change) karna hai. Hum koi nayi row nahi daal rahe hain, purani ko hi edit kar rahe hain.

2. SET empStatus = ?
Yeh batata hai ki table ke kis column ko badalna hai. Hum database se keh rahe hain ki empStatus naam ke column mein ek nayi value set kar do.

3. WHERE employeeId = ? (Sabse Important)
WHERE clause pure SQL ka sabse zaroori hissa hai. Yeh database ko batata hai ki kis specific employee ka status badalna hai.
-----------------------------------------------------------------------------------


()flow of this code:-
Bhai, ye code poori tarah se ek Identity Lifecycle Management Flow ko handle kar raha hai. 
Jab bhi SailPoint IIQ mein kisi employee ka status badalta hai (jaise naya joiner aana, 
transfer hona, ya company chhodna),

()ye code ek JDBC Provisioning Rule (Integration Rule) hai.
Iska kaam SailPoint IIQ aur aapke target database (jo ki ek Financial Planning & Forecasting naam ka system hai) ke beech
ek pul (bridge) banana hai. Jab bhi SailPoint mein kisi user ka account banta, badalta, ya delete hota hai, 
toh ye code seedhe database mein jaakar SQL query chalata hai.
Chalo isko step-by-step samajhte hain ki ye poori class background mein kya khel khel rahi hai:-

()Ye code basically do tables par kaam karta hai:-
employees (User ki main details aur status rakhne ke liye)
employee_roles (User ke paas kaun-kaun se roles/access hain, unka map rakhne ke liye)

Step 1: Main Traffic Control (doProvisioning):-
Sabse pehle SailPoint is class ke doProvisioning method ko call karta hai aur ek ProvisioningPlan (instructions ki list) bhejta hai.
Ye method check karta hai ki instructions kis application ke liye hain (Financial Planning & Forecasting).
Uske baad ye plan ke andar se Operation (Command) ko check karta hai aur aage sahi cabin (method) mein bhej deta hai:-
Agar order Create ka hai->createAccount() ko call karo.
Agar order Disable ka hai->disableAccount() ko call karo.
Agar order Modify (Badlaav) ka hai->modifyAccount() ko call karo.
Agar order Enable ka hai->enableAccount() ko call karo.

->>>>>>>>>>>>>>
Step 4: Account Block/Unblock Flows (disableAccount & enableAccount)
(a).Disable Account Flow (Job chhodne par):-
SailPoint se Disable command aate hi ye database mein jaakar user ki empStatus column ko "Inactive" update kar deta hai. 
Isse user ka login band ho jata hai.

(b).Enable Account Flow (Wapas aane par):-
SailPoint se Enable command aate hi ye database mein status ko wapas "Active" kar deta hai, 
aur suraksha ke liye dobara check karta hai ki uske paas default role hai ya nahi.
Agar nahi hota, toh wapas assign kar deta hai.

->>>>>>>>>>>>>>>>>>>>>>>>>
Step 3: Access Badalne Ka Flow (modifyAccount):-
Jab kisi user ka promotion, transfer, ya access change hota hai, toh SailPoint Modify command bhejta hai:-

Ye user ke attributeRequests ke andar jaakar sirf "roles" waale badlaav dhoondhta hai.

Role Add: Agar SailPoint ne bola hai Add ya Set,
toh ye wapas addRole function ko call karke database mein naya role entry kar deta hai.

Role Remove: Agar SailPoint ne bola hai Remove, 
toh ye removeRole function chalakar DELETE FROM employee_roles waali SQL query run karta hai.

Airtight Kill-Switch Check: Loop khatam hone par ye ek special check karta hai—isAnyRoleForEmployee(). 
Agar is user ke saare roles khatam ho chuke hain aur uske paas ek bhi access nahi bacha, 
toh ye automatic uske account ko disable kar deta hai taaki bina role ka koi fultu account active na rahe.

->>>>>>>>>>>>>>>>>>>>>>>>
Step 2: Naya Account Flow (createAccount):-
Jab SailPoint bolta hai ki "Naya user aaya hai, account banao":-
1. Plan se Data Nikalna (Data Parsing)
Sabse pehle, SailPoint ek package bhejta hai jise hum AccountRequest bolte hain. 
Iske andar naye user ki saari details hoti hain. Code un details ko ek-ek karke nikaalta hai:-

-->employeeId ko acctReq.getNativeIdentity() se nikaala jata hai (Ye har user ki unique ID hoti hai).
Baaki saare attributes jaise employee (username), firstName, lastName, manager, email ko .getValue().toString() karke 
normal String text mein badla jata hai.

2. Main Database Entry (The INSERT Query):-
INSERT INTO financial_planning_and_forecasting.employees 
(employeeId, employee, firstName, lastName, empStatus, department, ...) 
VALUES (?, ?, ?, ?, ?, ?, ...);
Code stmt.setString(1, employeeId), stmt.setString(2, employee) karke saari values ko serial number ke hisaab se ? ki jagah fit karta hai.
stmt.executeUpdate() chalte hi database ke employees table mein naye user ki ek fresh row (record) create ho jati hai.

3. Business Roles Assignment (The Loop):-
User ki profile toh ban gayi, par abhi uske paas koi taqat (roles) nahi hai. Uske liye code ek for loop chalata hai:
Role Check: Code check karta hai ki kya SailPoint ne is user ke sath koi "roles" bheje hain (Add ya Set operation ke sath)?
ID Mapping: Agar roles hain (jaise "Finance_Manager"), toh code pehle roles table mein jaakar query karta hai ki is naam ki roleId kya hai (roleIdByName).
Mapping Entry: roleId milte hi code use employee_roles naam ki mapping table mein daal deta hai (addRole function ke zariye).
[employee_roles Table]
───────────────────────────────────
employeeId      │  roleId
───────────────────────────────────
EMP101          │  ROL_FIN_MGR_002

4. Safety Audit Check (Default Role Security):-
Aakhiri mein code ek bahut hi zaroori check karta hai: checkExistingRole().
Code check karta hai ki "Kahin aisa toh nahi ki is naye user ko koi bhi role nahi mila?"
Agar user ka employee_roles table mein koi record nahi milta, toh code automatic use ek default view-only role chipka deta hai: ROL_FIN_AUDIT_VIEWER_010.
Iska fayda ye hai ki naya user platform par login toh kar payega, bhale hi use koi special access na mila ho.

()doProvisioning Kab aur Kaise Call Hota Hai?
Jab bhi SailPoint mein koi change hota hai jiske liye target system (jaise Active Directory, JDBC, ya Workday) 
mein data bhejna padta hai, toh SailPoint ek ProvisioningPlan banata hai.
Is plan ko execute karne ke liye SailPoint ka internal integration framework layer active hota hai 
aur woh automatically Integration Executor class ke doProvisioning(ProvisioningPlan plan) method ko call kar deta hai.
*//////////
