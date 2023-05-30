(function () {

    let categoriesList,personalMessage, updateModal, creationForm, pageOrchestrator = new PageOrchestrator();
    let oldName, newName;
    let categories = [];
    let updateQueue = [];

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "index.html";
        } else {
            pageOrchestrator.start(); // initialize the components
            pageOrchestrator.refresh(); // display initial content
        }
    }, false);

    function modifyName(event){
        var clickedListItem = event.target;
        var category = clickedListItem.category;

        var input = document.createElement("input");
        input.type = "text";
        input.value = category.name;

        clickedListItem.textContent = ""; // Rimuove il contenuto testuale precedente
        clickedListItem.appendChild(input);

        input.focus(); // Per mettere automaticamente il cursore nell'input

        input.addEventListener('blur', function() {
            category.name = input.value;

            /*var formElement = new FormData();
            formElement.append('name', newName);

            makeCall("POST", 'ModifyNameJS', formElement, function(req){
                if (req.readyState === XMLHttpRequest.DONE) {
                    if (req.status === 200) {
                        clickedListItem.textContent = category.id + " " + newName;
                    } else if (req.status === 403) {
                    window.location.href = req.getResponseHeader("Location");
                    window.sessionStorage.removeItem("username");
                    }
                }
            });*/
            clickedListItem.textContent = category.id + " " + category.name;
        });
    }

    function PersonalMessage(_name, _surname, messagecontainer) {
        this.name = _name;
        this.surname = _surname;
        this.show = function () {
            messagecontainer.textContent = "Nice to see you again, " + this.name + " " + this.surname;
        }
    }

    function CategoriesList(_allCategories){
        this.allCategories=_allCategories;

        this.reset = function () {
            this.allCategories.style.visibility = "hidden";
            //this.savebtn.style.display = "none";
        }

        this.show = function (){

            var self= this;
            makeCall('GET','/ImageCatalogationJS_war_exploded/GetCategoriesJS',null,
                function (req){

                    if (req.readyState === 4) {
                        var message = req.responseText;
                        if (req.status === 200) {
                            var categoryList = JSON.parse(message);
                            categories = [...categoryList];
                            /*if (categories.length == 0) {
                                self.alert.textContent = "Nessuna categoria";
                                return;
                            }
                            self.alert.textContent = "";*/
                            self.print(categories);
                        } else if (req.status === 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem("username");
                        } else {
                            //self.alert.textContent = message;
                        }
                    }
                });
        }

        this.print = function (tree){
            this.allCategories.innerHTML = "";
            var list = document.createElement("ul");

            tree.forEach(function(category) {
                var listItem = document.createElement("li");
                listItem.textContent = category.id + " " + category.name;

                listItem.category = category;

                listItem.addEventListener('click', modifyName);

                list.appendChild(listItem);
            });


            this.allCategories.appendChild(list);
            this.allCategories.style.visibility = "visible";
        }
    }

    function CreationForm(formId, _selector) {
        this.form = formId;
        this.selector = _selector;

        this.reset = function () {
            this.form.reset();
            this.selector.visibility = "hidden";
            this.enable();
        }

        this.disable = function () {
            this.form.querySelector("input[type='button'].submit").setAttribute("disabled", "true");
            this.form.querySelector("input[type='text']").setAttribute("disabled", "true");
            this.form.querySelector("select").setAttribute("disabled", "true");
        }

        this.enable = function () {
            this.form.querySelector("input[type='button'].submit").removeAttribute("disabled");
            this.form.querySelector("input[type='text']").removeAttribute("disabled");
            this.form.querySelector("select").removeAttribute("disabled");
        }

        this.registerEvents = function (orchestrator) {
            this.form.querySelector("input[type='button'].submit").addEventListener('click', (e) => {
                if (this.form.checkValidity()) {
                    var self = this;
                    if (updateQueue.length == 0) {

                        makeCall("POST", 'CreateCategoryJS', e.target.closest("form"),
                            function (req) {
                                if (req.readyState == XMLHttpRequest.DONE) {
                                    var message = req.responseText;
                                    if (req.status == 200) {
                                        orchestrator.refresh();
                                    } else if (req.status == 403) {
                                        window.location.href = req.getResponseHeader("Location");
                                        window.sessionStorage.removeItem("username");
                                    } else {
                                        self.reset();
                                    }
                                }
                            });
                    } else {
                        //window.alert("Non puoi aggiungere una categoria prima di aver salvato le modifiche");
                    }
                } else {
                    this.form.reportValidity();
                }
            })
        }
        this.show = function () {
            var self = this;
            makeCall("GET", '/ImageCatalogationJS_war_exploded/GetCategoriesJS', null,
                function (req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var categoryList = JSON.parse(message);
                            categories = [...categoryList];
                            self.update(categories);
                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem("username");
                        } else {
                            //self.alert.textContent = message;
                        }
                    }
                });
            this.enable();
        }

        this.update = function (arrayCategories) {
            var option;
            this.selector.innerHTML = ""; // empty the selection list
            var self = this;
            option = document.createElement("option");
            arrayCategories.forEach(function (category) {
                option = document.createElement("option");
                option.text = category.id + " " + category.name;
                option.value=category.id;
                self.selector.appendChild(option);
            });
            this.selector.style.visibility = "visible";
        }
    }


    function PageOrchestrator() {

        this.start = function () {

            personalMessage = new PersonalMessage(
                sessionStorage.getItem("name"),
                sessionStorage.getItem("surname"),
                document.getElementById("username"));
            personalMessage.show();

            categoriesList = new CategoriesList(
                document.getElementById("allCategories"));
           // categoriesList.registerEvents(this);
           // categoriesList.show();



            creationForm = new CreationForm(
                document.getElementById("creationForm"),
                document.getElementById("father"));
            creationForm.registerEvents(this);


        }

        this.refresh = function () {
            categoriesList.reset();
            creationForm.reset();
            categoriesList.show();
            creationForm.show();
            updateQueue=[];
        }
    }

})();