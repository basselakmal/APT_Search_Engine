/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var req;
var isIE;
var searchQuery;
var suggestions;
function init() {
        he = document.getElementById("he");
suggestions = document.getElementById("suggestions");
    searchQuery = document.getElementById("searchQuery");
}

function showSuggestion() {
               var url = "Suggestion?searchQuery=" + escape(searchQuery.value);
               console.log(searchQuery.value);

  
 req = initRequest();
        req.open("GET", url, true);
        req.onreadystatechange = callback;
        req.send(null);
}

function initRequest() {
    if (window.XMLHttpRequest) {
        if (navigator.userAgent.indexOf('MSIE') != -1) {
            isIE = true;
        }
        return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        isIE = true;
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
}
function callback() {
    if (req.readyState == 4) {
        if (req.status == 200) {
                    var allSuggestions = req.responseText;
                    var choices = allSuggestions.split("-")
    
      var string = "";
            for(var i = 0; i< choices.length - 1 ; i++)
        {
           string += '<option value = "' + choices[i] + '"/>';

         }
        
          suggestions.innerHTML = string;

        }
    }
}

