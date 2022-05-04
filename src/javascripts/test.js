window.onload = () =>{
    document.getElementById("search-button").addEventListener("click", function(event){
      event.preventDefault()
      fetch("/search?provider=IkeaTest&query=s")
        .then(response => response.text())
        .then(text => console.log(text));
    });
}