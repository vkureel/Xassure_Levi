package test.injection;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.xassure.dbControls.DatabaseControls;
import com.xassure.dbTables.employees;

public class TestDb {

	DatabaseControls dbControls;

	@Inject
	public TestDb(@Named("db") DatabaseControls dbControls) {
		this.dbControls = dbControls;
	}

	public void testing() {
		List<employees> emp = dbControls.readAllDataFromTable("employees");
		for (employees e : emp) {
			System.out.println(e.getfirstName());
		}

	}
}
