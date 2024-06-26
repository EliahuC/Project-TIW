





(function () {

    let categoriesList,personalMessage,confirmCopy,saveCopy,creationForm, pageOrchestrator = new PageOrchestrator();
    let destination,startElement,savebtn;
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


    function copyAndUpdate(_categoryID,_oldFatherId,_newFatherId) {
        var categoryID=_categoryID;
        var newFatherId=_newFatherId;
        var newId;
        makeCall("GET",'CopyCategoryJS?father='+newFatherId,null,
            function (req){
                if (req.readyState === 4) {
                    var message = req.responseText;
                    if (req.status === 200) {
                        newId=JSON.parse(message);
                        var j = categories.findIndex(c => c.id === categoryID);
                        var i = categories.findIndex(c => c.id === newFatherId);
                        var x=categories.findIndex(c=> c.id===newId);
                        while(x!==-1){
                            if(newId.substring(newId.length-1)==="9"){
                                self.alert("The number of sub-categories cannot be more than 9");
                                return;
                            }
                            newId=newId.substring(0,newId.length-1)+ (parseFloat(newId.substring(newId.length-1))+1) ;
                            x=categories.findIndex(c=> c.id===newId);
                        }
                        var newCopied = { ...categories[j] };
                        newCopied.id = newId;
                        var catChildren = categories.filter(function (c) { return c.id.substring(0, categoryID.length) === categoryID });
                        catChildren.forEach(function (child) {
                            i++;
                            var newChild = { ...child };
                            newChild.id = newId + child.id.substring(categoryID.length, child.id.length);
                            categories.splice(i,0,newChild);
                        });
                        categoriesList.print(categories.sort(function (c1,c2){
                            return (c1.id).localeCompare(c2.id);
                        }));
                        updateQueue.push({
                            categoryId: categoryID,
                            newId: newId,
                            newFatherId: newFatherId,
                        });
                    } else if (req.status === 403) {
                        window.location.href = req.getResponseHeader("Location");
                        window.sessionStorage.removeItem("username");
                    } else {
                        self.alert.textContent = message;
                    }
                }
            });



    }
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

            makeCall("POST", 'ModifyNameJS?name=' + category.name + '&categoryId=' + category.id, null,
                function (req){
                    if (req.readyState === XMLHttpRequest.DONE) {
                        if (req.status === 200) {
                            clickedListItem.textContent = category.id + " " + category.name;
                            creationForm.reset();
                            creationForm.show();
                        } else if (req.status === 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem("username");
                        }
                    }
                });
        });
    }

    function PersonalMessage(_name, _surname, messagecontainer) {
        this.name = _name;
        this.surname = _surname;
        this.show = function () {
            messagecontainer.textContent = "Nice to see you again, " + this.name + " " + this.surname;
        }
    }

    function CategoriesList(_allCategories,_savebtn, _alert) {
        this.allCategories = _allCategories;
        savebtn = _savebtn;
        this.alert = _alert;

        this.reset = function () {
            this.allCategories.style.visibility = "hidden";
            savebtn.style.display = "none";
        }

        this.show = function () {

            var self = this;
            makeCall('GET', '/ImageCatalogationJS_war_exploded/GetCategoriesJS', null,
                function (req) {

                    if (req.readyState === 4) {
                        var message = req.responseText;
                        if (req.status === 200) {
                            var categoryList = JSON.parse(message);
                            categories = [...categoryList];
                            if (categories.length === 0) {
                                self.alert.textContent = "Nessuna categoria";
                                return;
                            }
                            self.alert.textContent = "";
                            self.print(categories);
                        } else if (req.status === 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem("username");
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                });
        }

        this.print = function (tree) {
            this.allCategories.innerHTML = "";
            var list = document.createElement("ul");

            tree.forEach(function (category) {
                var listItem = document.createElement("li");
                listItem.textContent = category.id + " " + category.name;
                listItem.setAttribute("draggable", "true");
                listItem.addEventListener("dragstart", dragStartHandler);
                listItem.addEventListener("dragover", dragOverHandler);
                listItem.addEventListener("dragleave", dragLeaveHandler);
                listItem.addEventListener("drop", dropHandler);

                listItem.category = category;
                listItem.setAttribute('categoryId', category.id);


                listItem.addEventListener('click', modifyName);

                list.appendChild(listItem);
            });


            this.allCategories.appendChild(list);
            this.allCategories.style.visibility = "visible";
        }

        this.registerEvents = function (orchestrator) {
            savebtn.addEventListener('click', (e) => {
                if (updateQueue.length !== 0) {
                    var self = this;
                    makeCallJson("POST", 'SaveCategoriesJS', updateQueue,
                        function (req) {
                            if (req.readyState === XMLHttpRequest.DONE) {
                                var message = req.responseText;
                                if (req.status === 200) {
                                    orchestrator.refresh();
                                    saveCopy.show();
                                } else if (req.status === 403) {
                                    window.location.href = req.getResponseHeader("Location");
                                    window.sessionStorage.removeItem("username");
                                } else {
                                    self.alert.textContent = message;
                                    self.reset();
                                }
                            }
                        });
                }
            })
        }
    }

    function CreationForm(formId, _selector, _alert) {
        this.form = formId;
        this.selector = _selector;
        this.alert = _alert;

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
                    if (updateQueue.length === 0) {

                        makeCall("POST", 'CreateCategoryJS', e.target.closest("form"),
                            function (req) {
                                if (req.readyState === XMLHttpRequest.DONE) {
                                    var message = req.responseText;
                                    if (req.status === 200) {
                                        self.alert.textContent = "";
                                        orchestrator.refresh();
                                    } else if (req.status === 403) {
                                        window.location.href = req.getResponseHeader("Location");
                                        window.sessionStorage.removeItem("username");
                                    } else {
                                        self.alert.textContent = message;
                                        self.reset();
                                    }
                                }
                            });
                    } else {
                        window.alert("Illegal action");
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
                    if (req.readyState === 4) {
                        var message = req.responseText;
                        if (req.status === 200) {
                            var categoryList = JSON.parse(message);
                            categories = [...categoryList];
                            self.update(categories);
                        } else if (req.status === 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem("username");
                        } else {
                            self.alert.textContent = message;
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
    function dragStartHandler(event) {
        startElement = event.target.closest("li");
    }

    function dragOverHandler(event) {
        event.preventDefault();
        var destination = event.target.closest("li");
        destination.className = "selected";
    }

    function dragLeaveHandler(event) {
        var destination = event.target.closest("li");
        destination.className = "not-selected";
    }

    function dropHandler(event) {
        destination = event.target.closest("li");
        var categoryId = startElement.getAttribute("categoryId");
        var oldFatherId = startElement.getAttribute("categoryId");
        var newFatherId = destination.getAttribute("categoryId");
        oldFatherId=oldFatherId.substring(0,oldFatherId.length-1);
        var isAllowed = true;
        if (newFatherId === oldFatherId ||
            (newFatherId.substring(0, categoryId.length) === categoryId) ||
            newFatherId === categoryId) {
            isAllowed = false;
        }
        if (isAllowed) {
            confirmCopy.show();
        } else {
            destination.className = "not-selected";
            alert("Spostamento non consentito");
        }
    }

    function ConfirmCopy(_confirmCopy, _cancel, _confirm, _savebtn) {
        this.copy = _confirmCopy;
        this.cancel = _cancel;
        this.confirm = _confirm;
        this.savebtn = _savebtn;

        this.copy.querySelector("span").addEventListener('click', () => { this.close(); });
        this.cancel.addEventListener('click', () => { this.close(); });
        this.confirm.addEventListener('click', () => { this.confirm(); });

        this.show = function () {
            this.copy.style.display = "block";
            destination.className = "not-selected";
        }

        this.close = function () {
            this.copy.style.display = "none";
            destination.className = "not-selected";
        }

        this.confirm = function () {
            if (startElement && destination) {
                var categoryID = startElement.getAttribute("categoryId");
                var oldFatherId = startElement.getAttribute("categoryId");
                var newFatherId = destination.getAttribute("categoryId");
                oldFatherId=oldFatherId.substring(0,oldFatherId.length-1);
                var isAllowed = true;
                if (newFatherId === oldFatherId ||
                    (newFatherId.substring(0, categoryID.length) === categoryID) ||
                    newFatherId === categoryID) {
                    isAllowed = false;
                }
                if (isAllowed) {

                    copyAndUpdate(categoryID,oldFatherId,newFatherId);
                }
                this.savebtn.style.display = "inline-block";
                creationForm.disable();
            } else {
                this.close();
            }
            this.close();
        }

        this.reset = function () {
            this.copy.style.display = "none";
        }
    }

    function SaveCopy(_saveCopy) {
        this.copy = _saveCopy;

        this.copy.querySelector("span").addEventListener('click', () => { this.close(); });
        this.copy.querySelector("button").addEventListener('click', () => { this.close(); });

        this.show = function () {
            this.copy.style.display = "block";
        }

        this.close = function () {
            this.copy.style.display = "none";
        }

        this.reset = function () {
            this.copy.style.display = "none";
        }
    }


    function PageOrchestrator() {
        var alertContainer = document.getElementById("alert");

        this.start = function () {
            confirmCopy=new ConfirmCopy(
                document.getElementById("id_confirm"),
                document.getElementById("id_cancelbtn"),
                document.getElementById("id_confirmbtn"),
                document.getElementById("id_savebtn")
            );

            saveCopy=new SaveCopy(document.getElementById("id_save"));

            personalMessage = new PersonalMessage(
                sessionStorage.getItem("name"),
                sessionStorage.getItem("surname"),
                document.getElementById("username"));
            personalMessage.show();

            categoriesList = new CategoriesList(
                document.getElementById("allCategories"),
                document.getElementById("id_savebtn"),
                alertContainer);
            categoriesList.registerEvents(this);


            creationForm = new CreationForm(
                document.getElementById("creationForm"),
                document.getElementById("father"),
                alertContainer);
            creationForm.registerEvents(this);

        }

        this.refresh = function () {
            categoriesList.reset();
            creationForm.reset();
            categoriesList.show();
            creationForm.show();
            confirmCopy.reset();
            saveCopy.reset();
            destination=undefined;
            startElement = undefined;
            updateQueue=[];
        }
    }

})();