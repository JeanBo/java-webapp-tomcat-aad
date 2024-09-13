/*****************************************************************************
* Name        : Busy Indicator 
* Description : Shows the busy indicator when any Wicket Ajax call
*             : is made.  Include a div with id bysy_indicator
*             : which will be shown when a call is being made.
* Date		  Author   Description
* 14/08/2008  jzb0608  Initial, copied from Wicket site with some changes
* 26/09/2008  jzb0608  Fix javascript warning when the page doesn't use AJAX.
******************************************************************************/
window.onload = setupFunc;

function setupFunc() {
  document.getElementsByTagName('body')[0].onclick = clickFunc;
  hideBusysign();
  if (typeof Wicket != 'undefined') {

	  Wicket.Event.subscribe('/ajax/call/before', function(jqEvent, attributes, jqXHR, errorThrown, textStatus) {
		  showBusysign();
	  });
	  
	  Wicket.Event.subscribe('/ajax/call/done', function(jqEvent, attributes, jqXHR, errorThrown, textStatus) {
		  hideBusysign();
	  });

  }
}

function hideBusysign() {
  document.getElementById('bysy_indicator').style.display ='none';
}

function showBusysign() {
  document.getElementById('bysy_indicator').style.display ='inline';
}

function clickFunc(eventData) {

  var clickedElement = (window.event) ? event.srcElement : eventData.target;
  if ((clickedElement.tagName.toUpperCase() == 'BUTTON' && clickedElement.disabled!=true) 
  	|| (clickedElement.tagName.toUpperCase() == 'A' && clickedElement.className != 'disablebusyindicator')
  	|| (clickedElement.parentNode.tagName.toUpperCase() == 'A' && clickedElement.parentNode.className != 'disablebusyindicator')
    || (clickedElement.tagName.toUpperCase() == 'INPUT' 
    	&& (clickedElement.type.toUpperCase() == 'BUTTON' || clickedElement.type.toUpperCase() == 'SUBMIT')
    	&& clickedElement.disabled!=true)) {
    
    	/* Only if not disabled */
    	showBusysign();
    
  }
}
