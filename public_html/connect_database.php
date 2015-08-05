<?php 
require_once "login_details.php";
class Connect_database{
	function __construct(){

	}

	public function connect_db(){
		$conn = mysql_connect(Login_details::$SERVER_NAME, 
								Login_details::$USERNAME,
								Login_details::$PASSWORD);
		if ($conn->connect_error){
			die("connection_failed : ".$conn->connect_error);
		}
		mysql_select_db(Login_details::$DB_NAME, $conn);
		return $conn;
	}
}
?>