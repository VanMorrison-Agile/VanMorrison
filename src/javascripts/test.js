window.onload = () =>{
    document.getElementById("search-button").addEventListener("click", function(event){
      console.log("submit")
      let query = document.getElementById("search-bar").value;
      fetch("/search?provider=IkeaTest&query="+query)
        .then(response => response.text())
        .then(text => {
            console.log(text)
            document.getElementById("search-res").innerHTML = text;
        });
    });
}