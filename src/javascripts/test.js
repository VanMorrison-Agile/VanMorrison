window.onload = () =>{
    document.getElementById("search-form").addEventListener("submit", function(event){
      console.log("submit")
      event.preventDefault();
      let query = document.getElementById("search-bar").value;
      fetch("/search?provider=IkeaTest&query="+query)
        .then(response => response.text())
        .then(text => {
            console.log(text)
            document.getElementById("search-res").innerHTML = text;
        });
    });
}