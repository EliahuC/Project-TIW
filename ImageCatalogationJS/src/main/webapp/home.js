(function () {

    let categoriesList, creationWizard, pageOrchestrator = new PageOrchestrator();
    let categories = [];

    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "index.html";
        } else {
            pageOrchestrator.start(); // initialize the components
            pageOrchestrator.refresh(); // display initial content
        }
    }, false);

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
            var list = document.createElement("ul");

            tree.forEach(function(category) {
                var listItem = document.createElement("li");
                listItem.textContent = category.id + " " + category.name;
                list.appendChild(listItem);
            });

            this.allCategories.appendChild(list);
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
            //categoriesList.registerEvents(this);
            categoriesList.show();

            //creationWizard = new CreationWizard(
            //document.getElementById("creationWizard"),
            //document.getElementById("father"));
            //creationWizard.registerEvents(this);

        }

        this.refresh = function () {
            //categoriesList.reset();
            //creationWizard.reset();
           // categoriesList.show();
            //creationWizard.show();
        }
    }

})();