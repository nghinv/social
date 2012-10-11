/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

/**
 * UIComposer.js
 *
 */


var UIComposer = {
  onLoad: function(params) {
		UIComposer.configure(params);
		UIComposer.init();
  },
  handleShareButtonState: function(uiComposer) {
    if (uiComposer.minCharactersRequired !== 0) {
      //TODO hoatle handle backspace problem
      if (uiComposer.composer.val().length >= uiComposer.minCharactersRequired) {
        uiComposer.shareButton.className = 'ShareButtonDisable';
        if($("#ComposerContainer").length == 0){
          uiComposer.shareButton.removeAttr("disabled");
          uiComposer.shareButton.attr("class",'ShareButton');
        }
      } else {
        uiComposer.shareButton.css(background, '');
        uiComposer.shareButton.attr('class','ShareButton');
      }
    } else {
      if($("ComposerContainer").length == 0){
        uiComposer.shareButton.removeAttr("disabled");
        uiComposer.shareButton.attr("class",'ShareButton');
      }
    }

    if (uiComposer.maxCharactersAllowed !== 0) {
      if (uiComposer.composer.val().length >= uiComposer.maxCharactersAllowed) {
        //substitue it
        //TODO hoatle have a countdown displayed on the form
        uiComposer.composer.val( uiComposer.composer.val().substring(0, uiComposer.maxCharactersAllowed));
      }
    }
  },
  configure: function(params) {
    UIComposer.composerId = params.composerId || 'composerInput';
    UIComposer.defaultInput = params.defaultInput || "";
    UIComposer.minCharactersRequired = params.minCharactersRequired || 0;
    UIComposer.maxCharactersAllowed = params.maxCharactersAllowed || 0;
    UIComposer.focusColor = params.focusColor || '#000000';
    UIComposer.blurColor = params.blurColor || '#777777';
    UIComposer.minHeight = params.minHeight || '20px';
    UIComposer.focusHeight = params.focusHeight || '35px';
    UIComposer.maxHeight = params.maxHeight || '50px';
    UIComposer.padding = params.padding || '11px 0 11px 8px';
    UIComposer.focusCallback = params.focusCallback;
    UIComposer.blurCallback = params.blurCallback;
    UIComposer.keypressCallback = params.keypressCallback;
    UIComposer.postMessageCallback = params.postMessageCallback;
    UIComposer.userTyped = false;
    ;(function($, document, window){
   var portal = window.eXo.env.portal

   window.eXo.social = window.eXo.social || {};
   window.eXo.social.portal = {
     rest : (portal.rest) ? portal.rest : 'rest-socialdemo',
     portalName : (portal.portalName) ? portal.portalName : 'classic',
     context : (portal.context) ? portal.context : '/socialdemo',
     accessMode: (portal.accessMode) ? portal.accessMode : 'public',
     userName : (portal.userName) ? portal.userName : ''
   };
})(jQuery, document, window);
  },
  init: function() {
    UIComposer.composer = $('#' + UIComposer.composerId);
    UIComposer.shareButton = $('#ShareButton');
    if (!(UIComposer.composer && UIComposer.shareButton)) {
      alert('error: can not find composer or shareButton!');
    }
    
    UIComposer.composer.val(UIComposer.getValue());
    UIComposer.composer.css({'height':UIComposer.minHeight,
      'color':UIComposer.blurColor,
      'padding':UIComposer.padding
    });
    UIComposer.shareButton.attr('class','ShareButtonDisable');
    UIComposer.shareButton.attr('disabled',"disabled");
    
    if ( UIComposer.getValue() !== UIComposer.defaultInput ) {
      UIComposer.composer.css({'height' : UIComposer.maxHeight,
        'color'  : UIComposer.focusColor});
      UIComposer.shareButton.removeAttr("disabled");
      UIComposer.shareButton.attr("class",'ShareButton');
    }
    
    var isReadyEl = $("#isReadyId");
    var composerContainerEl = $("#ComposerContainer");
    var isReadyVal;
    UIComposer.composer.on('focus', function() {
      UIComposer.handleShareButtonState(UIComposer);
      if (UIComposer.composer.val() === UIComposer.defaultInput) {
        UIComposer.composer.val('') ;
      }
      if (UIComposer.focusCallback) {
        UIComposer.focusCallback();
      }
      UIComposer.composer.css({'height' : UIComposer.maxHeight,
        'color'  : UIComposer.focusColor});
    });

    UIComposer.composer.on('blur', function() {
      if (UIComposer.composer.val() === '') {
        UIComposer.composer.val(UIComposer.defaultInput);
        UIComposer.composer.css({'height' : UIComposer.minHeight,
                                 'color'  : UIComposer.blurColor});

        //if current composer is default composer then disable share button
        if(!UIComposer.isReady){
          UIComposer.shareButton.attr('disabled',"disabled");
          UIComposer.shareButton.attr('class','ShareButtonDisable');
        }
        
      } else {
        UIComposer.currentValue = UIComposer.composer.val();
      }

      if (UIComposer.blurCallback) {
        UIComposer.blurCallback();
      }
    });

    //
    $('textarea#composerInput').mentionsInput({
	    onDataRequest:function (mode, query, callback) {
	      var url = window.location.protocol + '//' + window.location.host + '/' + eXo.social.portal.rest + '/social/people/getprofile/data.json?search='+query;
	      $.getJSON(url, function(responseData) {
	        responseData = mentions.underscore._.filter(responseData, function(item) { return item.name.toLowerCase().indexOf(query.toLowerCase()) > -1 });
	        callback.call(this, responseData);
	      });
	    }
	
	  });
  },
  post: function() {
    UIComposer.composer.val(UIComposer.defaultInput);
    UIComposer.isReady = false;
    UIComposer.currentValue = UIComposer.defaultInput;
    UIComposer.init();
  },
  getValue: function() {
	  if (!UIComposer.currentValue) {
	    return UIComposer.defaultInput;
	  }
	  return UIComposer.currentValue;
	},
	setCurrentValue: function() {
	  var uiInputText = $("#" + UIComposer.composerId);
	  UIComposer.currentValue = uiInputText.val();
	},
	handleShareButton: function(isReadyForPostingActivity) {
	  UIComposer.isReady = isReadyForPostingActivity;
	  var shareButton = $("#ShareButton");
	  if ( isReadyForPostingActivity ) {
	    shareButton.removeAttr("disabled");
	    shareButton.attr("class",'ShareButton');
	  } else {
	    shareButton.attr("disabled","disabled");
	    shareButton.attr("class",'ShareButtonDisable');
	  }
	  
	  if ( UIComposer.currentValue !== UIComposer.defaultInput ) {
	    UIComposer.shareButton.removeAttr("disabled");
	    UIComposer.shareButton.attr("class",'ShareButton');
	  }
  }
}

window.UIComposer = UIComposer;
_module.UIComposer = UIComposer;