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
  handleShareButtonState: function() {
    if (UIComposer.minCharactersRequired !== 0) {
      //TODO hoatle handle backspace problem
      if (UIComposer.composer.val().length >= UIComposer.minCharactersRequired) {
        UIComposer.shareButton.className = 'ShareButtonDisable';
        if($("#ComposerContainer").length == 0){
          UIComposer.shareButton.removeAttr("disabled");
          UIComposer.shareButton.attr("class",'ShareButton');
        }
      } else {
        UIComposer.shareButton.css(background, '');
        UIComposer.shareButton.attr('class','ShareButton');
      }
    } else {
      if($("ComposerContainer").length == 0){
        UIComposer.shareButton.removeAttr("disabled");
        UIComposer.shareButton.attr("class",'ShareButton');
      }
    }

    if (UIComposer.maxCharactersAllowed !== 0) {
      if (UIComposer.composer.val().length >= UIComposer.maxCharactersAllowed) {
        //substitue it
        //TODO hoatle have a countdown displayed on the form
        UIComposer.composer.val( UIComposer.composer.val().substring(0, UIComposer.maxCharactersAllowed));
      }
    }
  },
  configure: function(params) {
    UIComposer.composerId = 'composerDisplay';
    UIComposer.defaultInput = params.defaultInput || "";
    UIComposer.minCharactersRequired = params.minCharactersRequired || 0;
    UIComposer.maxCharactersAllowed = params.maxCharactersAllowed || 0;
    UIComposer.focusColor = params.focusColor || '#000000';
    UIComposer.blurColor = params.blurColor || '#777777';
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

    UIComposer.shareButton.attr('class','ShareButtonDisable');
    UIComposer.shareButton.attr('disabled',"disabled");

    //
    $('textarea#composerInput').exoMentions({
        onDataRequest:function (mode, query, callback) {
          var url = window.location.protocol + '//' + window.location.host + '/' + eXo.social.portal.rest + '/social/people/getprofile/data.json?search='+query;
          $.getJSON(url, function(responseData) {
            responseData = mentions.underscore.filter(responseData, function(item) { 
              return item.name.toLowerCase().indexOf(query.toLowerCase()) > -1;
            });
            callback.call(this, responseData);
          });
        },
        idAction : "ShareButton",
        elasticStyle : {
          maxHeight : '38px',
          minHeight : '24px'
        },
        messages : {
          foundNoMatch : 'Found no matching users for',
          helpSearch: 'Type to start searching for users.'
        }
      });
  },
  post: function() {
    //UIComposer.composer.val(UIComposer.defaultInput);
    UIComposer.isReady = false;
    UIComposer.currentValue = "";
    UIComposer.init();
  },
  getValue: function() {
    return (UIComposer.currentValue) ? UIComposer.currentValue : '';
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
};

window.UIComposer = UIComposer;
_module.UIComposer = UIComposer;
