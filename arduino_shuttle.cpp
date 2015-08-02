//compatible with arduino compiler
/*
===================================================
Name :- Nishant Raj
Email :- raj.nishant360@gmail.com
College :- Indian School of Mines
Branch :- Computer Science and Engineering
Time :- 03 August 2015 (Monday) 02:00
===================================================*/
/*Following given code work for Arduino UNO with GSM900 and GPS bee module*/
#include <SoftwareSerial.h>
SoftwareSerial gprsSerial(2, 3);
int Gpsdata;             						// for incoming serial data 
unsigned int finish =0;  						// indicate end of message
unsigned int pos_cnt=0;  						// position counter
unsigned int flg    =0;  						// GPS flag 
unsigned int com_cnt=0;  						// comma counter
String lati="0";            					// latitude array
String lg="0";             						// longitude array
String tm="0";             						// time array
String save_lati , save_lg,save_spd;			// save lattitude , longitude , ans , speed for calculation
char validity;        							// validity of data
char dir1 , dir2;         						// direction N/E
String datei ="0";          		 			// date array
String spd ="0";								// save speed
char temp;
int long long count = 1;
void setup()
{
	gprsSerial.begin(9600);						// Start GPRS serial port at baud rate 9600 bits/sec
  	Serial.begin(9600);							// Start Arduino serial port at baud rate 9600 bits/sec

  	Serial.println("Config SIM900...");			// For testing in serial monitor comment during actual use
  	delay(2000);
  	Serial.println("Done!...");
  	gprsSerial.flush();
  	Serial.flush();

  	// attach or detach from GPRS service 
  	gprsSerial.println("AT+CGATT?"); /**Check if gprs is attached or not . If GPRS is attached then it will return 1 else 0 **/
  	delay(100);
  	toSerial();


  	// bearer settings
  	gprsSerial.println("AT+SAPBR=3,1,\"CONTYPE\",\"GPRS\"");
  	/*
		AT+SAPBR=<cmd_type>,<cid>[,<ConParamTag>,<ConParamValue>]
		<cmd_type>
			0 -> Close bearer
			1 -> Open bearer
			2 -> Query bearer
			3 -> Set bearer parameters
			4 -> Get bearer parameters
			5 -> Save the values of parameters to NVRAM

		<cid> 
			Bearer profile identifier

		<Status>
			0 -> Bearer is connecting
			1 -> Bearer is connected
			2 -> Bearer is closing
			3 -> Bearer is closed

		<ConParamTag> Bearer parameter
			"CONTYPE" 	-> Type of Internet connection. Value refer to <ConParamValue_ConType>
			"APN"     	-> Access point name string: maximum 50 characters
			"USER"    	-> User name string: maximum 50 characters
			"PWD"     	-> Password string: maximum 50 characters
			"PHONENUM"	-> Phone number for CSD call
			"RATE"      -> CSD connection rate. For value refer to <ConParamValue_Rate>

		<ConParamValue> Bearer paramer value

		<ConParamValue_ConType>
			"CSD"   -> Circuit-switched data call.
			"GPRS"  -> GPRS connection.
		
		<ConParamValue_Rate>
			0 -> 2400
			1 -> 4800
			2 -> 9600
			3 -> 14400

		<IP_Addr> The IP address of bearer
  	*/
  	delay(2000);
  	toSerial();

  	// bearer settings
  	gprsSerial.println("AT+SAPBR=3,1,\"APN\",\"airtelgprs.com\"");
  	delay(2000);
  	toSerial();

  	// bearer settings
  	gprsSerial.println("AT+SAPBR=1,1");
  	delay(2000);
  	toSerial();
}


void loop()
{
   	// initialize http service
	if(count%10 == 0){
     	count= 0 ;
     	gprsSerial.flush();
      	Serial.flush();
      	gprsSerial.println("AT+CIPSHUT");
      	/*
			AT+CIPSHUT Deactivate GPRS PDP Context
			If close is successful:
				SHUT OK
			If close fails:
				ERROR
      	*/

      	delay(100);
      	toSerial();
      	gprsSerial.println("AT+CGATT?");
      	/*
			AT+CGATT Attach or Detach from GPRS Service
			Read Command 
				 AT+CGATT?
					Response
				+CGATT: <state>
					OK

			Write Command 
				AT+CGATT=<state>
					Response -> OK
					If error is related to ME functionality: -> +CME ERROR: <err>
			Parameter
				<state> Indicates the state of GPRS attachment
					0 -> Detached
					1 -> Attached
      	*/

      	delay(100);
      	toSerial();
    
      	// attach or detach from GPRS service 
      	gprsSerial.println("AT+CGATT=1");
      	delay(100);
      	toSerial();
    
    
      	// bearer settings
      	gprsSerial.println("AT+SAPBR=3,1,\"CONTYPE\",\"GPRS\"");
      	delay(2000);
      	toSerial();
    
      	// bearer settings
      	gprsSerial.println("AT+SAPBR=3,1,\"APN\",\"airtelgprs.com\"");
      	delay(2000);
      	toSerial();
    
      	// bearer settings
      	gprsSerial.println("AT+SAPBR=1,1");
      	delay(2000);
      	toSerial();
   	}
   	count++;
   	Receive_GPS_Data();
   	gprsSerial.println("AT+HTTPINIT");
   	/*
		AT+HTTPINIT Initialize HTTP Service
		Response OK if Successfull else ERROR
   	*/
   	delay(2000); 
   	toSerial();

   	// set http param value
   	gprsSerial.println("AT+HTTPPARA=\"URL\",\"http://shuttletracker.hostei.com/?lat="+save_lati+"&long="+save_lg+"&speed="+save_spd+"\"");
   	/*
		AT+HTTPPARA Set HTTP Parameters Value

		Read Command -> AT+HTTPPARA?
		Response
			+ HTTPPARA:<HTTPParamTag>,<HTTPParamValue> OK

		Write Command -> AT+HTTPPARA=<HTTPParamTag>,<HTTPParamValue>
		
		<HTTPParamTag> HTTP Parameter
			"CID"		->		(Mandatory Parameter) Bearer profile identifier

			"URL"		->		(Mandatory Parameter) HTTP client URL
								"http://'server'/'path':'tcpPort' "
								"server": FQDN or IP-address
								"path": path of file or directory
								"tcpPort": default value is 80.
								Refer to "IETF-RFC 2616".

			"UA" 		-> 		The user agent string which is set by the application to identify the mobile. 
								Usually this parameter is set as operation system and software version information. 
								Default value is "SIMCOM_MODULE".

			"PROIP"		->		The IP address of HTTP proxy server

			"PROPORT"	->		The port of HTTP proxy server

			"REDIR"		->		This flag controls the redirection mechanism of the SIM900 when it is acting as HTTP client
								(numeric). If the server sends a redirect code (range 30x), the client will automatically send a
								new HTTP request when the flag is set to (1). Default value is 0 (no redirection).
			
			"BREAK"		->		Parameter for HTTP method "GET", used for resuming broken transfer.

			"BREAKEND"	->		Parameter for HTTP method "GET", used for resuming broken transfer. which is used together
								with "BREAK",If the value of "BREAKEND" is bigger than "BREAK", 
								the transfer scope is from "BREAK" to "BREAKEND".
								If the value of "BREAKEND" is smaller than "BREAK", the transfer scope is from "BREAK" to
								the end of the file.

			"TIMEOUT"	->		If both "BREAKEND" and "BREAK" are 0, the resume broken transfer function is disabled.
								HTTP session timeout value, scope: 30-1000 second.
								Default value is 120 seconds. HTTP Parameter value. 
								Type and supported content depend on related <HTTPParamTag>.
			"CONTENT"	->		Used to set the “Content-Type” field in HTTP header.

		<HTTPParamValue> -> HTTP Parameter value.Type and supported content depend on related <HTTPParamTag>.
   	*/

   	delay(2000);
   	toSerial();

   	// set http action type 0 = GET, 1 = POST, 2 = HEAD
   	gprsSerial.println("AT+HTTPACTION=0");
   	/*
		AT+HTTPACTION -> HTTP Method Action
		Test Command  -> AT+HTTPACTION=?
		Write Command -> AT+HTTPACTION=<Method>

		<Method> HTTP method specification:
			0 -> GET
			1 -> POST
			2 -> HEAD

		<StatusCode> HTTP Status Code responded by remote server, it identifier refer to HTTP1.1(RFC2616)
			100 Continue
			101 Switching Protocols
			200 OK
			201 Created
			202 Accepted
			203 Non-Authoritative Information
			204 No Content
			205 Reset Content
			206 Partial Content
			300 Multiple Choices
			301 Moved Permanently
			302 Found
			303 See Other
			304 Not Modified
			305 Use Proxy
			307 Temporary Redirect
			400 Bad Request
			401 Unauthorized
			402 Payment Required
			403 Forbidden
			404 Not Found
			405 Method Not Allowed
			406 Not Acceptable
			407 Proxy Authentication Required
			408 Request Time-out
			409 Conflict
			410 Gone
			411 Length Required
			412 Precondition Failed
			413 Request Entity Too Large
			414 Request-URI Too Large
			415 Unsupported Media Type
			416 Requested range not satisfiable
			417 Expectation Failed
			500 Internal Server Error
			501 Not Implemented
			502 Bad Gateway
			503 Service Unavailable
			504 Gateway Time-out
			505 HTTP Version not supported
			600 Not HTTP PDU
			601 Network Error
			602 No memory
			603 DNS Error
			604 Stack Busy

		<DataLen> the length of data got
   	*/
   	delay(6000);
   	toSerial();

   	// read server response
   	gprsSerial.println("AT+HTTPREAD");
   	/*
		AT+HTTPREAD 	-> Read the HTTP Server Response

		Test Command 	-> AT+HTTPREAD=?
		Response 		-> +HTTPREAD: (list of supported <start_address>s),(list of supported<byte_size>s) OK

		Write Command 	-> AT+HTTPREAD=<start_address>,<byte_size>
		Response
			+HTTPREAD: <date_len> <data> OK
			Read data when AT+HTTPACTION=0 or AT+HTTPDATA is executed.
			If<byte_size> is bigger than the data size received, module will only return actual data size.
			If error is related to ME functionality: +CME ERROR: <err>

		Parameters:-
			<data>			-> Data from HTTP server or user input.
			<start_address>	-> The starting point for data output. 1-318976 or 1-102400 (bytes), the max value is due to
								the module used.
			<byte_size>		-> The length for data output. 1-318976 or 1-102400 (bytes), the max value is due to
								the module used.
			<data_len>		-> The actual length for data output.

		Execution Command -> AT+HTTPREAD
		Response
			+HTTPREAD:<date_len><data> OK
			Read all data when AT+HTTPACTION=0 or AT+HTTPDATA is executed.
			If error is related to ME functionality: +CME ERROR: <err>
   	*/
   	delay(1000);
   	toSerial();

   	gprsSerial.println("");
   	gprsSerial.println("AT+HTTPTERM");
   	/*
		AT+HTTPTERM 	-> Terminate HTTP Service
		Test Command 	-> AT+HTTPTERM=?
		Response -> OK

		Execution command -> AT+HTTPTERM
		Response ->
			OK
			If error is related to ME functionality:+CME ERROR: <err>
   	*/
   	toSerial();
   	delay(300);

   	gprsSerial.println("");
   	delay(30000);
   	finish=0;pos_cnt = 0;
}

void toSerial()
{
	while(gprsSerial.available()!=0)
  	{
    	Serial.write(gprsSerial.read());
  	}
}
void Receive_GPS_Data()
{
	while(finish==0){
   		while(Serial.available()>0){         // Check GPS data 
       		Gpsdata = Serial.read();
       		temp = (char)Gpsdata;
       		//Serial.print(temp);
       		flg = 1;
       		if( temp=='$' && pos_cnt == 0)   // finding GPRMC header
     			pos_cnt=1;
   			if( temp=='G' && pos_cnt == 1)
     			pos_cnt=2;
   			if( temp=='P' && pos_cnt == 2)
     			pos_cnt=3;
   			if( temp=='R' && pos_cnt == 3)
     			pos_cnt=4;
   			if( temp=='M' && pos_cnt == 4)
     			pos_cnt=5;
   			if( temp=='C' && pos_cnt==5 )
     			pos_cnt=6;
   			if(pos_cnt==6 &&  temp ==','){   // count commas in message 
     			com_cnt++;
     			flg=0;
   			} 
   			if(com_cnt == 1 && flg == 1){
      			tm += temp;
      			flg = 0;
   			}
   			if(com_cnt == 2 && flg == 1){
      			validity = temp;
      			flg = 0;
   			}
   			if(com_cnt==3 && flg==1){
    			lati+=  temp;         // latitude
    			flg=0;
   			}
   			if(com_cnt == 4 && flg == 1){
      			dir1 = temp;
      			flg = 0;
   			}
   			if(com_cnt==5 && flg==1){
     			lg+=  temp;         // Longitude 
     			flg=0;
   			}
   			if(com_cnt == 6 && flg == 1){
      			dir2 = temp;
      			flg = 0;
   			}
   			if(com_cnt == 7 && flg == 1){
     			spd+= temp;
     			flg = 0;
   			}
   			if(com_cnt == 9 && flg == 1){
      			datei+= temp;
      			flg=0;
   			}
   			if( temp == '*' && com_cnt >= 5){
		        save_lati = lati;
		        save_spd = spd;
		        save_lg = lg;
		        lati.remove(0);
		        tm.remove(0);
		        lg.remove(0);
		        spd.remove(0);
		        datei.remove(0);
		        com_cnt = 0;
		        flg     = 0;
		        finish  = 1;
		        validity = 'L';
		        dir2 = 'A';
		        dir1 = 'A';
  			}
    	} 
 	}
}