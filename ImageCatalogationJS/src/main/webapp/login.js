/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("loginbutton").addEventListener('click', (e) => {
	  
    var form = e.target.closest("form");
    
    if (form.checkValidity()) {
		
      makeCall("POST", 'CheckLoginJS', e.target.closest("form"),
      
        function(x) {
          if (x.readyState == XMLHttpRequest.DONE) {
			  
            var message = x.responseText;
            
            switch (x.status) {
				
              case 200:
                var user = JSON.parse(message);
                sessionStorage.setItem("username", user.username);
                sessionStorage.setItem("name", user.name);
                sessionStorage.setItem("surname", user.surname);
                window.location.href = "Home.html";
                break;
                
              case 400: // bad request
                document.getElementById("errormessage").textContent = message;
                break;
                
              case 401: // unauthorized
                  document.getElementById("errormessage").textContent = message;
                  break;
                  
              case 500: // server error
            	document.getElementById("errormessage").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();