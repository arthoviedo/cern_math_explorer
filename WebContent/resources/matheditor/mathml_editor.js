//-------------------------------------------------------------
//	Created by: Ionel Alexandru 
//	Mail: ionel.alexandru@gmail.com
//	Site: www.fmath.info
//---------------------------------------------------------------

function getElement(id) {
	return document.getElementById ? document.getElementById(id) : document.all[id];
}

function resizeEditor(name, w, h){
	var obj = getElement("Div" + name);
	if(obj!=null){
		obj.style.width = parseFloat(w);
		obj.style.height = parseFloat(h);
	}
}

function getMathML(){
	alert("get mathml from javascript");
}

function saveMathML(value){
	alert(value);
}
