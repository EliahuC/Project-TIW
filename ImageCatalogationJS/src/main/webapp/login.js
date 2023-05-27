
(function(){

    document.getElementById("loginbutton").addEventListener('click', (e)=>{

        var form = e.target.closest("form");

        if (form.checkValidity()){

            makeCall("POST", '/ImageCatalogationJS_war_exploded/LoginJS', form,
                function(req){

                    if (req.readyState === XMLHttpRequest.DONE){

                        var message = req.responseText;

                        switch (req.status){

                            case 200: //OK
                                sessionStorage.setItem('username', message);
                                window.location.href = "Home.html";
                                break;

                            case 400: //Bad request
                                document.getElementById("errorMsg").textContent = message;
                                break;

                            case 401: //Unauthorized
                                document.getElementById("errorMsg").textContent = message;
                                break;

                            case 500: //Internal server error
                                document.getElementById("errorMsg").textContent = message;
                                break;
                        }
                    }
                });

        } else {
            form.reportValidity();
        }
    });

})();