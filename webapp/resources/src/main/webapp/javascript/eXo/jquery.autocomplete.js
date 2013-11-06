/*
 * Mentions Input use for ExoPlatform
 * Version 1.0.
 * Written by: Vu Duy Tu
 *
 * Using underscore.js
 *
 */

(function($, _, undefined) {
  // Settings
  var KEY = {
    BACKSPACE : 8,
    TAB : 9,
    RETURN : 13,
    ESC : 27,
    LEFT : 37,
    UP : 38,
    RIGHT : 39,
    DOWN : 40,
    DELETE : 46,
    COMMA : 188,
    SPACE : 32,
    HOME : 36,
    END : 35
  }; // Keys "enum"

  var defaultSettings = {
    templates : {
      wrapper : '<ul class="option-list dropdown-menu"></ul>',
      autocompleteListItem : '<li class="item"><a class="selected" href="javascript:void(0);"><%=text%></a></li>'),
    },
    source : {},
    maxItem: 10,
    delay : 300,
    focus : null,
    select : null,
    autoFocus: true
  };

  var eXoAutocomplete = function(settings) {

    settings = $.extend(true, {}, defaultSettings, settings);

    var element;
    
    element.on('click', click);
    element.on('keydown', keydown);
    element.on('keypress', keypress);
    element.on('keyup', keyup);
    element.on('paste', paste);
    element.on('blur', blur);
    
    function click(e) {
      
    };
    function keydown(e) {
      
    };
    function keypress(e) {
      
    };
    function keyup(e) {
      
    };
    function paste(e) {
      
    };
    function blur(e) {
      
    };
    
    var menu = {
        build : function() {
          
        },
        addItem : function(text) {
          
        },
        destroy : function() {
          
        },
        hide : function() {
          
        },
        show : function() {
          
        }
    };
    
    
    // Public methods
    return {
      init : function(domTarget) {
        element = $(domTarget);
      },
      show : function() {
        
      },
      hide : function() {
        
      }
    };
  };

  $.fn.eXoAutocomplete = function(method, settings) {

    var outerArguments = arguments;

    if (typeof method === 'object' || !method) {
      settings = method;
    }

    return this.each(function() {
      var instance = $.data(this, 'eXoAutocomplete') || $.data(this, 'eXoAutocomplete', new eXoAutocomplete(settings));
      if (_.isFunction(instance[method])) {
        return instance[method].apply(this, Array.prototype.slice.call(outerArguments, 1));
      } else if (typeof method === 'object' || !method) {
        return instance.init.call(this, this);
      } else {
        $.error('Method ' + method + ' does not exist');
      }
    });
  };

})(jQuery);
