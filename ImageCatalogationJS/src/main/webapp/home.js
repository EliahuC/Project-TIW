(function () {

    let categoriesList, creationWizard, pageOrchestrator = new PageOrchestrator();
    let allCategories = [];

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

    function CategoriesList(_listcontainer,_listcontainerbody,_savebtn){
        this.allCategories=_listcontainer;

    }


    function PageOrchestrator() {

        this.start = function () {

            personalMessage = new PersonalMessage(
                sessionStorage.getItem("name"),
                sessionStorage.getItem("surname"),
                document.getElementById("username"));
            personalMessage.show();

            //categoriesList = new CategoriesList(
                //document.getElementById("allCategories"));
            //categoriesList.registerEvents(this);

            //creationWizard = new CreationWizard(
                //document.getElementById("creationWizard"),
                //document.getElementById("father"));
            //creationWizard.registerEvents(this);

        }

        this.refresh = function () {
            //categoriesList.reset();
            //creationWizard.reset();
            //categoriesList.show();
            //creationWizard.show();
        }
    }

})();
