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

(function(eXo) {
  var portal = eXo.env.portal
  eXo.social = eXo.social || {};
  eXo.social.portal = {
    rest : (portal.rest) ? portal.rest : 'rest-socialdemo',
    portalName : (portal.portalName) ? portal.portalName : 'classic',
    context : (portal.context) ? portal.context : '/socialdemo',
    accessMode : (portal.accessMode) ? portal.accessMode : 'public',
    userName : (portal.userName) ? portal.userName : ''
  };
  eXo.social.I18n = eXo.social.I18n || {};
  eXo.social.I18n.mentions = eXo.social.I18n.mentions || {
    helpSearch: 'Type to start searching for users.',
    searching: 'Searching for ',
    foundNoMatch : 'Found no matching users for '
  };
})(window.eXo);

var UIComposer = {
  onLoadI18n : function(i18n) {
    window.eXo.social.I18n.mentions = $.extend(true, {}, window.eXo.social.I18n.mentions, i18n);
  },
  onLoad: function(params) {
    UIComposer.configure(params);
    UIComposer.init();
  },
  configure: function(params) {
    UIComposer.composerId = params.composerId;
    UIComposer.textareaId = params.textareaId;
    UIComposer.userTyped = false;
  },
  init: function() {
    UIComposer.composer = $('#' + UIComposer.composerId);

    $(document).ready(function() {
      var actionLink = $('#actionLink');
      if(actionLink.length > 0){
        if($('#InputLink').length == 0) {
          actionLink.trigger('click');
        } else {
          var container = $('#ComposerContainer');
          if(container.find('.LinkExtensionContainer').length > 0) {
            container.find('.LinkExtensionContainer').hide().data('isShow', {isShow: false});
          }
        }
      }
    });

    $('#UIComposer').find('.AttachMents').append(
        $('<a style="cursor: pointer; border:1px solid #E1E1E1; width: 25px; height: 15px;" id="test">&nbsp;@&nbsp;</a>')
     );
    //
    $('textarea#'+UIComposer.textareaId).exoMentions({
        onDataRequest:function (mode, query, callback) {
          var url = window.location.protocol + '//' + window.location.host + '/' + eXo.social.portal.rest + '/social/people/getprofile/data.json?search='+query;
          $.getJSON(url, function(responseData) {
            responseData = mentions.underscore.filter(responseData, function(item) { 
              return item.name.toLowerCase().indexOf(query.toLowerCase()) > -1;
            });
            callback.call(this, responseData);
          });
        },
        idAction : 'ShareButton',
        actionLink : 'AttachButton',
        actionMention : 'test',
        elasticStyle : {
          maxHeight : '38px',
          minHeight : '24px'
        },
        messages : window.eXo.social.I18n.mentions
      });
  },
  post: function() {
    UIComposer.isReady = false;
    UIComposer.currentValue = "";
   // UIComposer.init();
  },
  getValue: function() {
    return (UIComposer.currentValue) ? UIComposer.currentValue : '';
  },
  setCurrentValue: function() {
    var uiInputText =$('textarea#'+UIComposer.textareaId);
    UIComposer.currentValue = uiInputText.val();
  },
 
  showLink : function() {
  	var container = $('#ComposerContainer')
  	var link = container.find('.LinkExtensionContainer');
  	if(link.length > 0) {
  		if(link.data('isShow').isShow) {
  			link.hide().data('isShow', {isShow: false});
  		} else {
  			link.show().data('isShow', {isShow: true});
  		}
    } else {
      var cmp = container.find('.UILinkShareDisplay');
      if(cmp.length > 0) {
        $('textarea#composerInput').exoMentions('clearLink', function() {});
        $('#actionLink').trigger('click');
      }
    }
  }
};

window.UIComposer = UIComposer;
_module.UIComposer = UIComposer;
