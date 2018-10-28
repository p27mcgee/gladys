package com.github.p27mcgee.gladys.remoteconsole;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LedgerPostController {
	
	   @GetMapping("/gladys-remote-console")
	    public String index() {
	        return "gladys-remote-console is online.";
	    }	   
		
	   @PostMapping(path="/gladys-remote-console", consumes="text/json")
	    public String ledgerPost() {
	        return "OK";
	    }	
	   
	   @PostMapping("/gladys-remote-console/ledger")
	   public @ResponseBody ResponseEntity<String> post(@RequestBody String ledgerJson) {
		   System.out.println(ledgerJson);
		   // TODO use a logger
		   // TODO persist to DB
		   // TODO etc.
	       return new ResponseEntity<String>("OK", HttpStatus.OK);
	   }	   
}
