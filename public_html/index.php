<?php
include "connect_database.php";
$table_name = "bus_pos_1";
$obj = new Connect_database();
$conn = $obj->connect_db();

if (count($_GET)){
	if ($_GET['latest'] == null){
		$lat = $_GET['lat'];
		$long = $_GET['long'];
		$speed = $_GET['speed'];
		$v = date_create();
		$ts = $v->format('Y-m-d H:i:s');

		$q = "INSERT INTO ".$table_name." (latitude, longitude, speed, ts) ".
			"VALUES ('$lat', '$long', '$speed', '$ts')";
		mysql_query($q, $conn);
	}
	else{
		$q1 = "SELECT MAX( id ) FROM ". $table_name;
		$id_res = mysql_query($q1, $conn);
		$id_row = mysql_fetch_array($id_res);
		$id = $id_row[0];
		$q = "SELECT latitude, longitude, speed, ts FROM ".$table_name." WHERE id = '$id'";
		$res = mysql_query($q, $conn);
		while ($row = mysql_fetch_array($res)){
			$arr = array('latitude' => $row['latitude'],
						'longitude' => $row['longitude'],
						'speed' => $row['speed'],
						'ts' => $row['ts']);
			header('Content-type: application/json');
			echo json_encode($arr);
		}
	}
}
?>