/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var req;
var isIE;
var he;
var s;
var searchQuery;
var suggestions;
function init() {
        he = document.getElementById("he");
suggestions = document.getElementById("suggestions");
    searchQuery = document.getElementById("searchQuery");
}

function showSuggestion() {
   //     var url = "http://localhost:8084/APT_-_Search_Engine/"+ escape(searchWords.value);
               var url = "Suggestion?searchQuery=" + escape(searchQuery.value);
               console.log(searchQuery.value);

    //he.innerHTML = searchQuery.value;
     
    //      var url = "Search?action=complete&id=" + escape(searchWords.value);
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
       // he.innerHTML = req.responseText;
                    var allSuggestions = req.responseText;
                    var choices = allSuggestions.split("-")
       //     searchQuery.innerHTML = req.responseText + "<br>";
     
      //  he.innerHTML = choices;
                    //   he.innerHTML = choices + "<br>" +(choices.length - 1).toString() + "22222222222222222222:";
      var string = "";
            for(var i = 0; i< choices.length - 1 ; i++)
        {
           string += '<option value = "' + choices[i] + '"/>';
               //       string +=  choices[i] + "<br>";

         }
        
                      //string += choices
  // he.innerHTML = "awdada";
          //he.innerHTML = string;
          suggestions.innerHTML = string;

        }
    }
}

/*
function getXMLHttpRequest() {
	var xmlHttpReq = false;
	// to create XMLHttpRequest object in non-Microsoft browsers
	if (window.XMLHttpRequest) {
		xmlHttpReq = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		try {
			// to create XMLHttpRequest object in later versions
			// of Internet Explorer
			xmlHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
		} catch (exp1) {
			try {
				// to create XMLHttpRequest object in older versions
				// of Internet Explorer
				xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
			} catch (exp2) {
				xmlHttpReq = false;
			}
		}
	}
	return xmlHttpReq;
}
/*
 * AJAX call starts with this function
 
function makeRequest() {
	var xmlHttpRequest = getXMLHttpRequest();
	xmlHttpRequest.onreadystatechange = getReadyStateHandler(xmlHttpRequest);
	xmlHttpRequest.open("GET", "Search", true);
	xmlHttpRequest.setRequestHeader("Content-Type",
			"application/x-www-form-urlencoded");
	xmlHttpRequest.send(null);
}

/*
 * Returns a function that waits for the state change in XMLHttpRequest
 
function getReadyStateHandler(xmlHttpRequest) {

	// an anonymous function returned
	// it listens to the XMLHttpRequest instance
	return function() {
		if (xmlHttpRequest.readyState == 4) {
			if (xmlHttpRequest.status == 200) {
				document.getElementById("hello").innerHTML = xmlHttpRequest.responseText;
			} else {
				alert("HTTP error " + xmlHttpRequest.status + ": " + xmlHttpRequest.statusText);
			}
		}
	};
}*/
