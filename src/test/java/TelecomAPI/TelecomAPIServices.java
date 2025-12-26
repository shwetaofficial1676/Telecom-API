package TelecomAPI;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class TelecomAPIServices {
	private static String authToken;
	private static String contact_id;
	private static final String BASE_URL = "https://thinking-tester-contact-list.herokuapp.com"; // Replace with your
																									// actual base URL
	private static ExtentReports extent;
	private static ExtentTest test;

	@BeforeSuite
	public static void setup() {
		RestAssured.baseURI = BASE_URL;
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport_" + timestamp + ".html";

		ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
		spark.config().setDocumentTitle("Telecom API Service RestAssured Automation Report");
		spark.config().setReportName("Functional Tests");

		extent = new ExtentReports();
		extent.attachReporter(spark);
		extent.setSystemInfo("Tester", "Shwets M");
		extent.setSystemInfo("Environment", "QA");
		test = extent.createTest(
				"Add User,Get User Profile,Update User Data Partially,Login User,Add Contact,Get Contact List,Get Contact info,update Contact info,update Contact info partial,logout user");

	}

	@Test(priority = 1)
	public void addUser() {
		Response res = given().contentType(ContentType.JSON)
				.body("{\r\n" + "    \"firstName\": \"Shweta\",\r\n" + "    \"lastName\": \"M\",\r\n"
						+ "    \"email\": \"shweta@yaho18.com\",\r\n" + "    \"password\": \"yahoo123\"\r\n" + "}")
				.when().post("/users");
		test.info("Post method with body");
		res.then().log().body();
		// validation on status code
		authToken = res.jsonPath().getString("token");

		Assert.assertEquals(res.getStatusCode(), 201, "Status code are not matched please verify");
		test.pass("Added User");
	}

	@Test(priority = 2)
	public void getUserProfile() {
		Response response = RestAssured.given().header("Authorization", "Bearer " + authToken)
				.header("Content-Type", "application/json").when().get("/users/me");
		test.info("Get method with Bearer Token");
		response.then().log().body();

		Assert.assertEquals(response.getStatusCode(), 200, "Status code are not matched please verify");
		test.pass("User Profile fetched!");
	}

	@Test(priority = 3)
	public void updateUserPartial() {
		Response response = given().header("Authorization", "Bearer " + authToken) // Manual header approach
				// OR use: .auth().oauth2(bearerToken)
				.contentType(ContentType.JSON).body("{\r\n" + "    \"firstName\": \"Sahana\"\r\n" + "}").when()
				.patch("/users/me") // The endpoint for the resource
				.then().statusCode(200) // Validate success code
				.log().body().extract().response();
		test.info("Patch method with Bearer Token");
		Assert.assertEquals(response.getStatusCode(), 200, "Status code are not matched please verify");
		test.pass("Updated User Partial Data!!");
	}

	@Test(priority = 4)
	public void loginUser() {
		Response res = given().contentType(ContentType.JSON).body(
				"{\r\n" + "    \"email\": \"shweta@yaho12.com\",\r\n" + "    \"password\": \"yahoo123\"\r\n" + "}")
				.when().post("/users/login");
		res.then().log().body();
		// validation on status code
		authToken = res.jsonPath().getString("token");
		test.info("Post method with Email and Password");
		Assert.assertEquals(res.getStatusCode(), 200, "Status code are not matched please verify");
		test.pass("User Logged In Successfully!!");
	}

	@Test(priority = 5)
	public void addContact() {
		Response res = given().contentType(ContentType.JSON).header("Authorization", "Bearer " + authToken) // Manual
																											// header
																											// approach
				.body("{\r\n" + "    \"firstName\": \"Ishan\",\r\n" + "    \"lastName\": \"M\",\r\n"
						+ "    \"birthdate\": \"1970-01-01\",\r\n" + "    \"email\": \"sham@fake.com\",\r\n"
						+ "    \"phone\": \"8005555555\",\r\n" + "    \"street1\": \"1 Main St.\",\r\n"
						+ "    \"street2\": \"Apartment A\",\r\n" + "    \"city\": \"Anytown\",\r\n"
						+ "    \"stateProvince\": \"KS\",\r\n" + "    \"postalCode\": \"12345\",\r\n"
						+ "    \"country\": \"IND\"\r\n" + "}")
				.when().post("/contacts");
		res.then().log().body();
		test.info("Post method with Bearer Token");
		// validation on status code
		Assert.assertEquals(res.getStatusCode(), 201, "Status code are not matched please verify");
		test.pass("Add Contact API Passed Success!!");
	}

	@Test(priority = 6)
	public void getContactList() {
		Response response = RestAssured.given().header("Authorization", "Bearer " + authToken)
				.header("Content-Type", "application/json").when().get("/contacts");
		response.then().log().body();
		test.info("Get method with Bearer Token");
		contact_id = response.jsonPath().getString("[0]._id");
		Assert.assertEquals(response.getStatusCode(), 200, "Status code are not matched please verify");
		test.pass("Contact List API Success!!");
	}

	@Test(priority = 7)
	public void getContact() {
		System.out.println(contact_id);
		Response response = RestAssured.given().header("Authorization", "Bearer " + authToken)
				.header("Content-Type", "application/json").when().get("/contacts/" + contact_id);
		response.then().log().body();
		test.info("Get method with Bearer Token");
		Assert.assertEquals(response.getStatusCode(), 200, "Status code are not matched please verify");
		test.pass("Get Contact  API Success!!");
	}

	@Test(priority = 8)
	public void updateContact() {
		Response response = given().header("Authorization", "Bearer " + authToken).contentType(ContentType.JSON)
				.body("{\r\n" + "    \"firstName\": \"Suyam\",\r\n" + "    \"lastName\": \"K\",\r\n"
						+ "    \"birthdate\": \"1992-02-02\",\r\n" + "    \"email\": \"Suyam@fake.com\",\r\n"
						+ "    \"phone\": \"8005554242\",\r\n" + "    \"street1\": \"13 School St.\",\r\n"
						+ "    \"street2\": \"Apt. 5\",\r\n" + "    \"city\": \"Washington\",\r\n"
						+ "    \"stateProvince\": \"QC\",\r\n" + "    \"postalCode\": \"A1A1A1\",\r\n"
						+ "    \"country\": \"Canada\"\r\n" + "}")
				.when().put("/contacts/" + contact_id) // The endpoint for the resource
				.then().statusCode(200) // Validate success code
				.log().body().extract().response();
		test.info("Put method with Bearer Token with body data");
		Assert.assertEquals(response.getStatusCode(), 200, "Status code are not matched please verify");
		test.pass("Updated Contact Data!!");
	}

	@Test(priority = 9)
	public void updateContactPartial() {
		Response response = given().header("Authorization", "Bearer " + authToken).contentType(ContentType.JSON)
				.body("{\r\n" + "    \"firstName\": \"Sahana\"\r\n" + "}").when().patch("/contacts/" + contact_id) // The
																													// endpoint
																													// for
																													// the
																													// resource
				.then().statusCode(200) // Validate success code
				.log().body().extract().response();
		test.info("Patch method with Bearer Token with body data");
		Assert.assertEquals(response.getStatusCode(), 200, "Status code are not matched please verify");
		test.pass("Updated Contact Data Partially!!");
	}

	@Test(priority = 10)
	public void logOutUser() {
		Response res = given().header("Authorization", "Bearer " + authToken).contentType(ContentType.JSON).when()
				.post("/users/logout");
		res.then().log().body();
		test.info("Post method with Bearer Token");
		// validation on status code
		Assert.assertEquals(res.getStatusCode(), 200, "Status code are not matched please verify");
		test.pass("Logged out user!!");
	}

	@AfterSuite
	public void generateReport() {
		extent.flush();
	}
}
