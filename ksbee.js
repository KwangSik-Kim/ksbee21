
var ksbee = (function() {
	var version = 0.01;
	var __dummyCount__	= 0;


	function getVersion() {
		return version;
	};

	function makeDummyID(evtObj, preStr, postStr) {
		if ( !preStr ) {
			preStr = "usel";
		}
		if ( !postStr ) {
			postStr = "uchk";
		}

		return ("__"+preStr+"__"+ parseInt(new Date().getTime()) +"__"+postStr+"__"+__dummyCount__++);
	};

	function clearEventBubble(evtObj) {
		if ( !evtObj ) {
			return false;
		}
		evtObj.preventDefault();
		evtObj.stopPropagation();
		evtObj.cancelBubble = true;
		return false;
	};

	function isDomElement( obj ) {
		if ( !obj ) {
			return false;
		}
		return ( obj instanceof Element );
	}

	function isJson( obj ) {
		if ( !obj ) {
			return false;
		}
		return ( obj.constructor === {}.constructor );
	}

	function isMap( obj ) {
		if ( !obj ) 
			return false;
		return ( obj instanceof Map );
	}

	function isSet( obj ) {
		if ( !obj ) 
			return false;
		return ( obj instanceof Set );
	}

	function isString( obj ) {
		if ( obj === null || obj === undefined ) 
			return false;
		if ( obj === '' ) 
			return true;
		return ( (typeof obj) === "string" );
	}

	function isNumber( obj ) {
		if ( obj === null || obj === undefined ) 
			return false;
		return ( (typeof obj) === "number" );
	}

	function isFunction( obj ) {
		if ( obj === null || obj === undefined ) 
			return false;
		return ( (typeof obj) === "function" );
	}

	function appendDomEvents(targetObj, fnMap ) {
		if ( !isDomElement(targetObj)  || !isMap(fnMap) ) {
			return false;
		}
		fnMap.forEach(function(v,k,obj) {
			if ( isString(k) && isFunction(v) ) {
				targetObj.addEventListener(k,v,false);
			}
		});
		return true;
	}

	function executeTriggerEvents(targetObj, evtName ) {
		if ( isDomElement(targetObj) && isString(evtName) ) {
			if ( "createEvent" in document ) {
				let evtObj = document.createEvent("Event");
				evtObj.initEvent(evtName,true,true);
				targetObj.dispatchEvent(evtObj);
				return true;
			} else {
				targetObj.fireEvent("on"+evtName);
			}
		}
		return false;
	}

	function tranlateMapToObjectsRecursive( mapObj ) {
		var result = new Object();
		if ( !(isMap(mapObj) || Array.isArray(mapObj)) ) {
			return result;
		}
		if ( Array.isArray(mapObj)) {
			result = [];
			for ( let i = 0, iSize = arrayObj.length; i < iSize; i++ ) {
				let v = arrayObj[i];
				if ( isMap(v) ) {
					result.push(tranlateMapToObjectsRecursive(v));
				} else if ( Array.isArray(v)) {
					result.push(translateArrayToObjectsRecursive(v));
				} else {
					result.push(v);
				}
			}
			return result;
		}
		mapObj.forEach(function(v,k,obj) {
			if ( isMap(v) ) {
				result[k] = tranlateMapToObjectsRecursive(v);
			} else if ( Array.isArray(v)) {
				result[k] = translateArrayToObjectsRecursive(v);
			} else {
				result[k] = v;
			}
		});
		return result;
	}

	function translateArrayToObjectsRecursive ( arrayObj ) {
		var result = [];
		if ( !Array.isArray(arrayObj) ) {
			return result;
		}
		for ( let i = 0, iSize = arrayObj.length; i < iSize; i++ ) {
			let v = arrayObj[i];
			if ( isMap(v) ) {
				result.push(tranlateMapToObjectsRecursive(v));
			} else if ( Array.isArray(v)) {
				result.push(translateArrayToObjectsRecursive(v));
			} else {
				result.push(v);
			}
		}
		return result;
	}

	function tranlateMapToObjects( mapObj ) {
		var result = new Object();
		if ( !(isMap(mapObj) || Array.isArray(mapObj)) ) {
			return result;
		}
		if ( Array.isArray(mapObj) ) {
			return translateArrayToObjectsRecursive(mapObj);
		}
		mapObj.forEach(function(v,k,obj) {
			if ( isMap(v) ) {
				result[k] = tranlateMapToObjectsRecursive(v);
			} else if ( Array.isArray(v)) {
				result[k] = translateArrayToObjectsRecursive(v);
			} else {
				result[k] = v;
			}
		});
		return result;
	}

	function tranlateJsonToMapRecursive( jObj ) {
		var result = new Map();
		if ( !isJson(jObj) ) {
			return result;
		}

		for ( let keys in jObj ) {
			let v = jObj[keys];
			if ( isJson(v) ) {
				result.set(keys,tranlateJsonToMapRecursive(v));
			} else if ( Array.isArray(v)) {
				result.set(keys,translateArrayToMapRecursive(v));
			} else {
				result.set(keys,v);
			}
		}
		return result;
	}

	function translateArrayToMapRecursive( aObj ) {
		var result = [];
		if ( !Array.isArray(aObj) )	{
			return result;
		}
		for ( let i = 0, iSize = aObj.length; i < iSize; i++ ) {
			let v = aObj[i];
			if ( isJson(v) ) {
				result.push(tranlateJsonToMapRecursive(v));
			} else if ( Array.isArray(v) ) {
				result.push(translateArrayToMapRecursive(v));
			} else {
				result.push(v);
			}
		}
		return result;
	}

	function tranlateArrayToMap( jObj ) {
		var result = [];
		if ( isJson(jObj) ) {
			return tranlateJsonToMap(jObj);
		}
		if ( !Array.isArray(jObj) ) {
			return result;
		}
		for ( let i = 0, iSize = jObj.length; i < iSize; i++ ) {
			let v = jObj[i];
			if ( isJson(v) ) {
				result.push(tranlateJsonToMapRecursive(v));
			} else if ( Array.isArray(v)) {
				result.push(translateArrayToMapRecursive(v));
			} else {
				result.push(v);
			}
		}
		return result;
	}


	function tranlateJsonToMap( jObj ) {
		var result = new Map();
		if ( Array.isArray(jObj) ) {
			return tranlateArrayToMap(jObj);
		}
		if ( !isJson(jObj) ) {
			return result;
		}
		for ( let keys in jObj ) {
			let v = jObj[keys];
			if ( isJson(v) ) {
				result.set(keys,tranlateJsonToMapRecursive(v));
			} else if ( Array.isArray(v)) {
				result.set(keys,translateArrayToMapRecursive(v));
			} else {
				result.set(keys,v);
			}
		}
		return result;
	}

	function makeTreeUIFromArrayRecursive(ulObj,tObj,optObj,depth) {
		for ( let i = 0, iSize = tObj.length; i < iSize; i++ ) {
			let v = tObj[i];
			if ( !isMap(v) ) {
				continue;
			}
			let title = v.get(optObj.get("NAME"));
			let childObj = v.get(optObj.get("CHILD"));
			let blockCls	= v.get(optObj.get("BLOCK_CLS"));

			if ( !blockCls ) {
				blockCls = "___nested___";
				//___active___;
			}
			let liObj = document.createElement("LI");
//			liObj.innerHTML = title;
			ulObj.appendChild(liObj);
			if ( Array.isArray(childObj)) {
				liObj.appendChild(makeFoldUnFoldUI());
				liObj.appendChild(makeCheckLabelUI(null,title, null));

				let nUlObj = document.createElement("UL");
				nUlObj.classList.add(blockCls);
				liObj.appendChild(nUlObj);
				makeTreeUIFromArrayRecursive(nUlObj, childObj, optObj, (depth+1));
			} else if ( isMap(childObj) ) {
				liObj.appendChild(makeFoldUnFoldUI());
				liObj.appendChild(makeCheckLabelUI(null,title, null));

				let nUlObj = document.createElement("UL");
				nUlObj.classList.add(blockCls);
				liObj.appendChild(nUlObj);
				makeTreeUIFromMapRecursive(nUlObj, childObj, optObj, (depth+1));
			} else {
				liObj.appendChild(makeFoldUnFoldUI(true));
				liObj.appendChild(makeCheckLabelUI(null,title, null));
			}
		}
	}

	function makeTreeUIFromMapRecursive(ulObj,tObj,optObj,depth) {
		
		if ( !isMap(tObj) ) {
			return;
		}
		let blockCls	= optObj.get(optObj.get("BLOCK_CLS"));

		if ( !blockCls ) {
			blockCls = "___nested___";
			//___active___;
		}

		if ( tObj.has(optObj.get("NAME") ) && tObj.has(optObj.get("CHILD")) ) {
			let title = tObj.get(optObj.get("NAME"));
			let childObj = tObj.get(optObj.get("CHILD"));
			let liObj = document.createElement("LI");
//			liObj.innerHTML = title;
			ulObj.appendChild(liObj);
			if ( Array.isArray(childObj)) {
				liObj.appendChild(makeFoldUnFoldUI());
				liObj.appendChild(makeCheckLabelUI(null,title, null));

				let nUlObj = document.createElement("UL");
				nUlObj.classList.add(blockCls);
				liObj.appendChild(nUlObj);
				makeTreeUIFromArrayRecursive(nUlObj, childObj, optObj, (depth+1));
			} else if ( isMap(childObj) ) {
				liObj.appendChild(makeFoldUnFoldUI());
				liObj.appendChild(makeCheckLabelUI(null,title, null));

				let nUlObj = document.createElement("UL");
				nUlObj.classList.add(blockCls);
				liObj.appendChild(nUlObj);
				makeTreeUIFromMapRecursive(nUlObj, childObj, optObj, (depth+1));
			} else {
				liObj.appendChild(makeFoldUnFoldUI(true));
				liObj.appendChild(makeCheckLabelUI(null,title, null));
			}
			return;
		}
	

		tObj.forEach(function(v,k,obj) {
			if ( isMap(v) ) {
				let title = v.get(optObj.get("NAME"));
				let childObj = v.get(optObj.get("CHILD"));
				let liObj = document.createElement("LI");
	//			liObj.innerHTML = title;
				ulObj.appendChild(liObj);
				if ( Array.isArray(childObj)) {
					liObj.appendChild(makeFoldUnFoldUI());
					liObj.appendChild(makeCheckLabelUI(null,title, null));

					let nUlObj = document.createElement("UL");
					nUlObj.classList.add(blockCls);
					liObj.appendChild(nUlObj);
					makeTreeUIFromArrayRecursive(nUlObj, childObj, optObj, (depth+1));
				} else if ( isMap(childObj) ) {
					liObj.appendChild(makeFoldUnFoldUI());
					liObj.appendChild(makeCheckLabelUI(null,title, null));

					let nUlObj = document.createElement("UL");
					nUlObj.classList.add(blockCls);
					liObj.appendChild(nUlObj);
					makeTreeUIFromMapRecursive(nUlObj, childObj, optObj, (depth+1));
				} else {
					liObj.appendChild(makeFoldUnFoldUI(true));
					liObj.appendChild(makeCheckLabelUI(null,title, null));
				}
			}
		});
	}

	function makeFoldUnFoldUI( isLeaf, symbolName, clsName ) {
		let spanObj = document.createElement("SPAN");
		if ( !clsName ) {
			clsName = "__fold_unfold__";
		}
		if ( clsName ) {
			spanObj.classList.add(clsName);
		}
		if ( !symbolName ) {
			symbolName = "&#9193;";
//			symbolName = "&#9195;";
		}
		if ( isLeaf ) {
			symbolName = "&nbsp;"
			symbolName = "&#160;";
			symbolName = "&#160;";
		}
		if ( symbolName ) {
			spanObj.innerHTML = symbolName;
		}
		return spanObj;
	}

	function makeCheckLabelUI( mainObj, titleName, clsName, lblClsName ) {
		let inObj = document.createElement("INPUT");
		inObj.type="checkbox";

		if ( !clsName ) {
			clsName = "___ksbee_span_check_ui___";
		}

		if ( !lblClsName ) {
			lblClsName = "___ksbee_span_check_label_ui___";
		}

		if ( clsName ) {
			inObj.classList.add(clsName);
		}
		let idStr = makeDummyID();
		inObj.setAttribute("id",idStr);
		let labelObj = document.createElement("LABEL");
		labelObj.setAttribute("for",idStr);

		if (lblClsName ) {
			labelObj.classList.add(lblClsName);
//			labelObj.classList.add("partial_checked");
		}
		labelObj.innerText = titleName;
		if ( !mainObj ) {
			mainObj = document.createElement("SPAN");
		}
		mainObj.appendChild(inObj);
		mainObj.appendChild(labelObj);
		return mainObj;
	}
	
	function makeTreeUI(tgtObj, optObj) {
		if ( !optObj ) {
			optObj = new Map();
			optObj.set("NAME","TITLE");
			optObj.set("CHILD","CHILD");
		}

		if ( isJson(optObj) ) {
			optObj = tranlateJsonToMap(optObj);
		}
		if ( !isMap(optObj) ) {
			optObj = new Map();
			optObj.set("NAME","TITLE");
			optObj.set("CHILD","CHILD");
		}

		if ( !optObj.has("NAME")) {
			optObj.set("NAME","TITLE");
		}

		if (!optObj.has("CHILD") ) {
			optObj.set("CHILD","CHILD");
		}

		let ulObj = document.createElement("UL");
		if ( Array.isArray(tgtObj)) {
			makeTreeUIFromArrayRecursive(ulObj,tranlateArrayToMap(tgtObj),optObj,0);
		} else if ( isJson(tgtObj) ) {
			makeTreeUIFromMapRecursive(ulObj,translateJsonToMap(tgtObj),optObj,0);
		} else if ( isMap(tgtObj) ) {
			makeTreeUIFromMapRecursive(ulObj,tgtObj,optObj,0);			
		}

		if ( optObj.has("CLASSNAME") ) {
			ulObj.classList.add(optObj.get("CALASSNAME"));
		} else {
			ulObj.classList.add("ksbee___tree");
		}

		ulObj.addEventListener("click",executeCommonTreeClickEvent,false);
		ulObj.addEventListener("mousedown",executeCommonTreeMouseDownEvent,false);

		let idStr = makeDummyID();
		tree.addContextMenu(idStr,optObj.get("contextMenuObj"));
		ulObj.dataset.contextMenuObj = idStr;

		return ulObj;
	}

	function getNearbyParentElements( sourceObj, tagName ) {
		if ( !sourceObj || !tagName ) {
			return undefined;
		}

		var pObj = sourceObj.parentElement;
		while ( pObj ) {
			if ( pObj.tagName === tagName ) {
				break;
			}
			pObj = pObj.parentElement;
		}
		return pObj;
	}

	function executeCommonTreeClickEvent(evtObj) {
		let mObj = evtObj.currentTarget;
		let cObj = evtObj.target || evtObj.srcElement;
		if ( cObj.nodeName === "SPAN" && cObj.classList.contains("__fold_unfold__") ) {
			let ulObj = cObj.parentElement.querySelector("UL");
			if ( !ulObj ) {
				return;
			}
			ulObj.classList.toggle("___nested___");
			if ( ulObj.classList.contains("___nested___") ) {
				cObj.innerHTML = "&#9193;";
			} else {
				cObj.innerHTML = "&#9195;";
			}

//			symbolName = "&#9193;";
//			symbolName = "&#9195;";

//			ulObj.classList.toggle("___active___");
		} else if ( cObj.nodeName === "INPUT" && cObj.type == "checkbox" ) {
			cObj.nextElementSibling.classList.remove("partial_checked");
			let liObj = getNearbyParentElements(cObj, "LI");
			if ( liObj ) {
				let subUlObj = liObj.querySelector("UL");
				if ( subUlObj ) {
					let chkArray = subUlObj.querySelectorAll("INPUT[type=checkbox]");
					if ( chkArray ) {
						for ( let t = 0, tSize = chkArray.length; t < tSize; t++ ) {
							chkArray[t].checked = cObj.checked;
							chkArray[t].nextElementSibling.classList.remove("partial_checked");
						}
					}
				}
				let cLiObj = getNearbyParentElements(liObj,"LI");
				let partialFlag = false;
				let tCount = 0;
				while ( cLiObj ) {

					if ( ++tCount > 10 ) {
						alert ( "MAX TCOUNT BREAK " );
						break;
					} 
					let cChkObj = cLiObj.querySelector("INPUT[type=checkbox]");
					console.log(cChkObj.id + " :" + tCount );
					if ( partialFlag ) {
						cChkObj.checked = false;
						cChkObj.nextElementSibling.classList.add("partial_checked");
						cLiObj = getNearbyParentElements(cLiObj,"LI");
						continue;
					}

					let cUlObj = cLiObj.querySelector("UL");
					let cChkArray = cUlObj.querySelectorAll("INPUT[type=checkbox]");
					for ( let t = 0, tSize = cChkArray.length; t < tSize; t++ ) {
						if ( cChkArray[t].checked != cObj.checked ) {
							partialFlag = true;
							break;
						}
					}
					if ( partialFlag ) {
						cChkObj.checked = false;
						cChkObj.nextElementSibling.classList.add("partial_checked");
					} else {
						cChkObj.nextElementSibling.classList.remove("partial_checked");
						cChkObj.checked = cObj.checked;
					}

					cLiObj = getNearbyParentElements(cLiObj,"LI");
				}
			}
		}
	}

	function executeCommonTreeMouseDownEvent(evtObj) {
		let mainObj = evtObj.currentTarget;
		let cObj = evtObj.target || evtObj.srcElement;
		if ( evtObj.which === 3 ) {
			alert('CTX MENU -->> ' + cObj.innerText );
			if ( mainObj.dataset.contextMenuObj ) {
				let obj = mainObj.dataset.contextMenuObj;
				let ctxMenu = tree.getContextMenu(obj);
				if ( ctxMenu ) {
					ctxMenu.showContextMenu(evtObj.pageX, evtObj.pageY);
				}
			}
		}

	}


	function getCurrentViewPortSize() {
		let xw = window.innerWidth;
		let yh = window.innerHeight;
		if ( typeof xw !== "number" ) {
			if ( document.documentElement ) {
				xw = document.documentElement.clientWidth;
				yh = document.documentElemetn.clientHeight;
			} else {
				xw = document.body.clientWidth;
				yh = document.body.clientHeight;
			}
		}
		return [xw,yh];
	}

	function getElementOffsetPosition(obj) {
		if ( !obj ) {
			return undefined;
		}
		let xw = parseFloat(obj.offsetLeft);
		let yh = parseFloat(obj.offsetTop);

		obj = obj.offsetParent;
		while ( obj ) {
			xw += parseFloat(obj.offsetLeft);
			yh += parseFloat(obj.offsetTop);
			obj = obj.offsetParent;
		}
		return [xw,yh];
	}

	function getCurrentPageXY() {
		let docObj = document.documentElement;
		let px = parseFloat((window.pageXOffset || docObj.scrollLeft)) - parseFloat(docObj.clientWidth || 0);
		let py = parseFloat((window.pageYOffset || docObj.scrollTop)) - parseFloat(docObj.clientHeight || 0);
		return [px, py];
	}

	function testM( e) {
		alert ( getCurrentPageXY() + "\n" + getCurrentViewPortSize() + " \n" + getElementOffsetPosition(document.getElementById("bbb"))+"\n" + e.x + " , " + e.y + "\n" + e.pageX + " , " + e.pageY 
		+ "\n" + e.clientX + " , " + e.clientY);
	}


	function executeFadeInObject( obj, delta, target, timeMillis ) {
		if ( !isDomElement(obj) ) {
			return;
		}
		if ( !isNumber(delta) ) {
			delta = 0.01;
		}
		if ( !isNumber(target) ) {
			target = 1;
		}
		if ( !isNumber(timeMillis) ) {
			timeMillis = 10;
		}
		let orgStyle = obj.getAttribute("___originalOpacityStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalOpacityStyle___",obj.style.opacity);
		}
		orgStyle = obj.getAttribute("___originalDisplayStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalDisplayStyle___",obj.style.display);
		}
		obj.style.opacity = 0;
		executeFadeInObjectTimer(obj,delta,target,timeMillis);
	}

	function executeFadeInObjectTimer( obj, delta, target, timeMillis ) {
		if ( !isDomElement(obj) ) {
			return;
		}
		let cValue = parseFloat(obj.style.opacity);
		cValue += delta;
//		alert(cValue + " : " + delta + " : " + target );
		if ( cValue >= target ) {
			let orgStyle = obj.getAttribute("___originalOpacityStyle___");
			obj.style.opacity = orgStyle;
//			alert ("Finished");
		} else {
			obj.style.opacity = cValue;
			setTimeout(function() {
				executeFadeInObjectTimer(obj,delta,target,timeMillis);
			}, timeMillis);
		}
	}

	function executeFadeOutObject( obj, delta, target, timeMillis ) {
		if ( !isDomElement(obj) ) {
			return;
		}
		if ( !isNumber(delta) ) {
			delta = 0.01;
		}
		if ( !isNumber(target) ) {
			target = 0;
		}
		if ( !isNumber(timeMillis) ) {
			timeMillis = 10;
		}
		let orgStyle = obj.getAttribute("___originalOpacityStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalOpacityStyle___",obj.style.opacity);
		}
		orgStyle = obj.getAttribute("___originalDisplayStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalDisplayStyle___",obj.style.display);
		}
		obj.style.opacity = 1;
		executeFadeOutObjectTimer(obj,delta,target,timeMillis);
	}

	function executeFadeOutObjectTimer( obj, delta, target, timeMillis ) {
		if ( !isDomElement(obj) ) {
			return;
		}
		let cValue = parseFloat(obj.style.opacity);
		cValue -= delta;
		if ( cValue <= target ) {
			obj.style.opacity = 0;
			if ( target < 0 ) {
				obj.style.display = "none";
			}
//			alert ("Finished - OUT");
		} else {
			obj.style.opacity = cValue;
			setTimeout(function() {
				executeFadeOutObjectTimer(obj,delta,target,timeMillis);
			}, timeMillis);
		}
	}

	function executeSlideDownObject( obj, delta, target, timeMillis ) {
		if ( !isDomElement(obj) ) {
			return;
		}
		if ( !isNumber(delta) ) {
			delta = 10;
		}
		if ( !isNumber(timeMillis) ) {
			timeMillis = 10;
		}
		let orgStyle = obj.getAttribute("___originalHeightStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalHeightStyle___",obj.style.height);
		}
		orgStyle = obj.getAttribute("___originalDisplayStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalDisplayStyle___",obj.style.display);
		}

		orgStyle = obj.getAttribute("___originalOverflowStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalOverflowStyle___",obj.style.overflow);
		}

		orgStyle = obj.getAttribute("___originalComuptedHeightStyle___");
		if (!isString(orgStyle)) {
			objStyle = window.getComputedStyle(obj).getPropertyValue("height");
			obj.setAttribute("___originalComuptedHeightStyle___",objStyle);
		}
		if ( !isNumber(target) ) {
			target = parseFloat(objStyle);
		}
		obj.style.height = "0px";
		obj.style.overflow = "hidden";
		obj.style.display = "block";
		executeSlideDownObjectTimer(obj,delta,target,timeMillis);
	}

	function executeSlideDownObjectTimer( obj, delta, target, timeMillis ) {
		if ( !isDomElement(obj) ) {
			return;
		}
		let cValue = parseFloat(obj.style.height);
		cValue += delta;
//		alert(cValue + " : " + delta + " : " + target );
		if ( cValue >= target ) {
			obj.style.height = target+"px";
			let orgStyle = obj.getAttribute("___originalHeightStyle___");
			obj.style.height = orgStyle;
			orgStyle = obj.getAttribute("___originalOverflowStyle___");
			obj.style.overflow = orgStyle;
		} else {
			obj.style.height = cValue+"px";
			setTimeout(function() {
				executeSlideDownObjectTimer(obj,delta,target,timeMillis);
			}, timeMillis);
		}
	}


	function executeSlideUpObject( obj, delta, target, timeMillis ) {
		if ( !isDomElement(obj) ) {
			return;
		}
		if ( !isNumber(delta) ) {
			delta = 10;
		}
		if ( !isNumber(timeMillis) ) {
			timeMillis = 10;
		}
		let orgStyle = obj.getAttribute("___originalHeightStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalHeightStyle___",obj.style.height);
		}
		orgStyle = obj.getAttribute("___originalDisplayStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalDisplayStyle___",obj.style.display);
		}

		orgStyle = obj.getAttribute("___originalOverflowStyle___");
		if (!isString(orgStyle)) {
			obj.setAttribute("___originalOverflowStyle___",obj.style.overflow);
		}


		orgStyle = obj.getAttribute("___originalComuptedHeightStyle___");
		if (!isString(orgStyle)) {
			objStyle = window.getComputedStyle(obj).getPropertyValue("height");
			obj.setAttribute("___originalComuptedHeightStyle___",objStyle);
		} else {
			let objStyle02 = window.getComputedStyle(obj).getPropertyValue("height");
			if ( parseFloat(orgStyle) < parseFloat(objStyle02) ) {
				objStyle = objStyle02;
				obj.setAttribute("___originalComuptedHeightStyle___",objStyle);
			}
		}

		if ( !isNumber(target) ) {
			target = -1;
		}
		obj.style.overflow = "hidden";
		obj.style.display = "block";
		executeSlideUpObjectTimer(obj,delta,target,timeMillis);
	}

	function executeSlideUpObjectTimer( obj, delta, target, timeMillis ) {
		if ( !isDomElement(obj) ) {
			return;
		}
		let cValue = parseInt(window.getComputedStyle(obj).getPropertyValue("height"));
		cValue -= delta;
		if ( cValue <= target ) {
			obj.style.height = 0+"px";
			if ( target < 0 ) {
				obj.style.display = "none";
			}
			orgStyle = obj.getAttribute("___originalOverflowStyle___");
			obj.style.overflow = orgStyle;

//			alert ("Finished - OUT");
		} else {
			obj.style.height = cValue+"px";
			setTimeout(function() {
				executeSlideUpObjectTimer(obj,delta,target,timeMillis);
			}, timeMillis);
		}
	}



	var ContextMenu = function() {
		var storage = new Map();
		var optObj	= new Map();

		var modalObj		= undefined;
		var mainDiv			= undefined;

		var defaultOptions = new Map();
		defaultOptions.set("modalBlockClass", "___ksbee_context_modal_block_ui___");
		defaultOptions.set("mainBlockClass", "___ksbee_context_main_block_ui___");
		defaultOptions.set("menuBlockClass", "___ksbee_context_menu_block_ui___");
		defaultOptions.set("menuItemClass", "___ksbee_context_menu_items_ui___");
		defaultOptions.set("menuSubItemClass", "___ksbee_context_menu_subitems_ui___");

		this.initContextMenu = function(jsonObj) {
			if ( jsonObj ) {
				storage = tranlateJsonToMap(jsonObj);
			}

			if ( !optObj ) {
				optObj = new Map();
				optObj.set("NAME","TITLE");
				optObj.set("CHILD","CHILD");
			}

			if ( isJson(optObj) ) {
				optObj = tranlateJsonToMap(optObj);
			}
			if ( !isMap(optObj) ) {
				optObj = new Map();
				optObj.set("NAME","TITLE");
				optObj.set("CHILD","CHILD");
			}

			if ( !optObj.has("NAME")) {
				optObj.set("NAME","TITLE");
			}

			if (!optObj.has("CHILD") ) {
				optObj.set("CHILD","CHILD");
			}

			mainDiv = makeMainBlockUI(0,0);
			mainDiv.style.display = "none";
			if ( Array.isArray(storage) ) {
				makeContextUIFromArrayRecursive(mainDiv,storage, optObj, 0);
			} else if ( isMap(storage) ) {
				makeContextUIFromMapRecursive(mainDiv,storage, optObj, 0);
			} else {
				makeContextUIFromMapRecursive(mainDiv,storage, optObj, 0);
			}
			mainDiv.addEventListener("click",executeContextMenuDefaultClick,false);
		};

		this.getMainContext = function(px,py) {
			if ( modalObj ) {
				return modalObj;
			}
			
			mainDiv.style.left = px+"px";
			mainDiv.style.top = py + "px";
/*
			if ( baseObj ) {
				baseObj.appendChild(mainDiv);
			}
*/
			return mainDiv;
		};

		this.showContextMenu = function(px,py) {
			mainDiv.style.left = px+"px";
			mainDiv.style.top = py + "px";
			mainDiv.style.display = "block";
		};

		this.hideContextMenu = function() {
			mainDiv.style.left = px+"px";
			mainDiv.style.top = py + "px";
			mainDiv.style.display = "none";
		};


		function makeContextUIFromArrayRecursive(mainBlockUI,tObj,optObj,depth) {
			for ( let i = 0, iSize = tObj.length; i < iSize; i++ ) {
				let v = tObj[i];
				if ( !isMap(v) ) {
					continue;
				}
				let title = v.get(optObj.get("NAME"));
				let childObj = v.get(optObj.get("CHILD"));
				let blockCls	= v.get(optObj.get("BLOCK_CLS"));

				if ( !blockCls ) {
					blockCls = "___nested___";
					//___active___;
				}
				let menuBlock = makeMenuBlockUI();
				mainBlockUI.appendChild(menuBlock);
				if ( Array.isArray(childObj)) {
					menuBlock.appendChild(makeItemBlocks(title, true));
					let sMenuBlock = makeSubItemBlockUI();
					let sMainBlock = makeMainBlockUI(200,-30);
					sMenuBlock.appendChild(sMainBlock);
					menuBlock.appendChild(sMenuBlock);
					makeContextUIFromArrayRecursive(sMainBlock, childObj, optObj, (depth+1));
				} else if ( isMap(childObj) ) {
					menuBlock.appendChild(makeItemBlocks(title, true));
					let sMenuBlock = makeSubItemBlockUI();
					let sMainBlock = makeMainBlockUI(200,-30);
					sMenuBlock.appendChild(sMainBlock);
					menuBlock.appendChild(sMenuBlock);
					
					makeContextUIFromMapRecursive(sMainBlock, childObj, optObj, (depth+1));
				} else {
					menuBlock.appendChild(makeItemBlocks(title, false));
				}
			}
		}

		function makeContextUIFromMapRecursive(mainBlockUI,tObj,optObj,depth) {
			
			if ( !isMap(tObj) ) {
				return;
			}
			let blockCls	= optObj.get(optObj.get("BLOCK_CLS"));

			if ( !blockCls ) {
				blockCls = "___nested___";
				//___active___;
			}

			let menuBlock = makeMenuBlockUI();
			mainBlockUI.appendChild(menuBlock);


			if ( tObj.has(optObj.get("NAME") ) && tObj.has(optObj.get("CHILD")) ) {
				let title = tObj.get(optObj.get("NAME"));
				let childObj = tObj.get(optObj.get("CHILD"));
				if ( Array.isArray(childObj)) {
					menuBlock.appendChild(makeItemBlocks(title, true));
					let sMenuBlock = makeSubItemBlockUI();
					let sMainBlock = makeMainBlockUI(200,-30);
					sMenuBlock.appendChild(sMainBlock);
					menuBlock.appendChild(sMenuBlock);
					
					makeContextUIFromArrayRecursive(sMainBlock, childObj, optObj, (depth+1));
				} else if ( isMap(childObj) ) {
					menuBlock.appendChild(makeItemBlocks(title, true));
					let sMenuBlock = makeSubItemBlockUI();
					let sMainBlock = makeMainBlockUI(200,-30);
					sMenuBlock.appendChild(sMainBlock);
					menuBlock.appendChild(sMenuBlock);
					
					makeContextUIFromMapRecursive(sMainBlock, childObj, optObj, (depth+1));
				} else {
					menuBlock.appendChild(makeItemBlocks(title, false));
				}
				return;
			}

			tObj.forEach(function(v,k,obj) {
				if ( isMap(v) ) {
					let title = v.get(optObj.get("NAME"));
					let childObj = v.get(optObj.get("CHILD"));
					let liObj = document.createElement("LI");
					if ( Array.isArray(childObj)) {
						menuBlock.appendChild(makeItemBlocks(title, true));
						let sMenuBlock = makeSubItemBlockUI();
						let sMainBlock = makeMainBlockUI(200,-30);
						sMenuBlock.appendChild(sMainBlock);
						menuBlock.appendChild(sMenuBlock);
						makeContextUIFromArrayRecursive(sMainBlock, childObj, optObj, (depth+1));
					} else if ( isMap(childObj) ) {
						menuBlock.appendChild(makeItemBlocks(title, true));
						let sMenuBlock = makeSubItemBlockUI();
						let sMainBlock = makeMainBlockUI(200,-30);
						sMenuBlock.appendChild(sMainBlock);
						menuBlock.appendChild(sMenuBlock);
						makeContextUIFromMapRecursive(sMainBlock, childObj, optObj, (depth+1));
					} else {
						menuBlock.appendChild(makeItemBlocks(title, false));
					}
				}
			});
		}


		function makeTextNode(str) {
			if ( !str ) {
				str = "";
			}
			let spObj = document.createElement("SPAN");
			let txtNode = document.createTextNode(str);
			spObj.appendChild(txtNode);
			return spObj;
		}

		function makeFoldUI(str) {
			if (!str ) {
				str = "c";
				str = "&#x029D0;";
				str = "&#9666;";
				str = "&#9656;";
			}
			let aObj = document.createElement("A");
			aObj.setAttribute("href","javascript:void(0);");
			aObj.innerHTML = str;
			return aObj;
		}

		function makeItemBlocks(str,hasChild) {
			let itemObj = document.createElement("DIV");
			itemObj.setAttribute("class",defaultOptions.get("menuItemClass"));
			itemObj.appendChild(makeTextNode(str));
			if ( hasChild ) {
				itemObj.appendChild(makeFoldUI());
			}
			return itemObj;
		}

		function makeMenuBlockUI() {
			let mBlockObj = document.createElement("DIV");
			mBlockObj.setAttribute("class",defaultOptions.get("menuBlockClass"));
			return mBlockObj;
		}

		function makeSubItemBlockUI() {
			let mainObj = document.createElement("DIV");
			mainObj.setAttribute("class",defaultOptions.get("menuSubItemClass"));
			return mainObj;
		}


		function makeMainBlockUI(left,top) {
			let mainObj = document.createElement("DIV");
			mainObj.setAttribute("class",defaultOptions.get("mainBlockClass"));
			mainObj.style.left = left+"px";
			mainObj.style.top = top+"px";
			return mainObj;
		}

		function executeContextMenuDefaultClick(evtObj) {
			if ( !evtObj ) {
				return;
			}
			let mainObj = evtObj.currentTarget;
			let cObj	= evtObj.target || evtObj.srcElement;

			if ( mainObj === cObj ) {
				mainObj.style.display = "none";
			}

			if ( cObj.nodeName === "A" ) {
				let pObj = getNearbyParentElements(cObj,"DIV");
				let nObj = pObj.nextElementSibling;
//				alert ( pObj.getAttribute("class") + " : " + nObj.getAttribute("class") );
				if ( nObj && nObj.nodeName === "DIV" && nObj.classList.contains(defaultOptions.get("menuSubItemClass")) ) {
					let dis = window.getComputedStyle(nObj).getPropertyValue('display');
					if ( dis && dis === "none" ) {
						nObj.style.display = "block";
						let str = "&#9666;";
//						let str = "&#9656;";

						cObj.innerHTML = str;
					} else {
						nObj.style.display = "none";
//						let str = "&#9666;";
						let str = "&#9656;";

						cObj.innerHTML = str;
					}
					return clearEventBubble();			
				}
			} else if ( cObj.nodeName === "SPAN" ) {
				if ( confirm("Hide context menu ?")) {
					mainObj.style.display = "none";
				} else {
					alert("Menu Item Clicked : " + cObj.innerText );
				}
			}
			return true;
		}

	};

	var ctxMenuMap	= new Map();

	var evt = {
		executeTriggerEvents : executeTriggerEvents,
		appendDomEvents : appendDomEvents,
	};

	var fn = {};
	var data = {
		tranlateJsonToMap : tranlateJsonToMap,
		tranlateArrayToMap : tranlateArrayToMap, 
		tranlateMapToObjects : tranlateMapToObjects,
	};
	var ui = {
		executeFadeInObject : executeFadeInObject,
		executeFadeOutObject : executeFadeOutObject,
		executeSlideDownObject : executeSlideDownObject,
		executeSlideUpObject : executeSlideUpObject,
	};
	var grid = ui.grid = {};
	var tree = ui.tree = {
		makeTreeUI : makeTreeUI,
		addContextMenu : function(id,ctxMenu) {
			if (ctxMenuMap.has(id) ) {
				return false;
			}
			ctxMenuMap.set(id,ctxMenu);
		},
		getContextMenu : function(id) {
			return ctxMenuMap.get(id);
		},
	};



	var context = ui.context = {
		ContextMenu : ContextMenu,
	};


	return {
		getVersion : getVersion,
		evt : evt,
		fn : fn,
		data : data,
		ui : ui,
	};

})();