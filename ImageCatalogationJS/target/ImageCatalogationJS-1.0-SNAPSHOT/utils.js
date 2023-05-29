
 function makeCall(method, url, formElement, cback, reset = true) {
    var req = new XMLHttpRequest(); // visible by closure
    req.onreadystatechange = function() {
      cback(req)
    }; // closure
    req.open(method, url);
    if (formElement == null) {
      req.send();
    } else {
      req.send(new FormData(formElement));
    }
    if (formElement !== null && reset === true) {
      formElement.reset();
    }
  }

  function makeCallJson(method, url, body, cback) {
    var req = new XMLHttpRequest(); // visible by closure
    req.onreadystatechange = function() {
      cback(req)
    }; // closure
    req.open(method, url);
    if (body == null) {
      req.send();
    } else {
      req.send(JSON.stringify(body));
    }
  }