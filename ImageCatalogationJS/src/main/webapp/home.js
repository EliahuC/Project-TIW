(function(){
	let categoryForm,showCategories,pageOrchestrator = new pageOrchestrator(); 
	let allCategories =[];
	
	
	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		} else {
			pageOrchestrator.start(); // initialize the components
			pageOrchestrator.refresh(); // display initial content
		}
	}, false);

	
	
	
	
	
})