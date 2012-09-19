
(function() { 

  var UIImageCropper = {
    init: function(params) {
      var cropLabel = params.cropLabel || null;
      var reCropLabel = params.reCropLabel || null;
      var selectionExists;
      
			gj('img#profileAvatar').imageCrop({
		    displaySize : true,
		    overlayOpacity : 0.25,
		
		    onSelect : updateForm
		  });
		  
	    function updateForm(crop) {
		    gj('input#x').val(crop.selectionX);
		    gj('input#y').val(crop.selectionY);
		    gj('input#width').val(crop.selectionWidth);
		    gj('input#height').val(crop.selectionHeight);
		    
		    selectionExists = crop.selectionExists();
		    
		    if ( selectionExists ) {
		      gj('a#cropAction').removeAttr("disabled");
		    } else {
		      gj('a#cropAction').unbind('click');
		      gj('a#cropAction').attr('disabled',"disabled");
		    }
	    };

	    gj('a#cropAction').on('click', function() {
        if ( $(this).attr('data') === 'crop' ) {
          $(this).text(reCropLabel);
          $(this).attr('data', 'recrop')
        } else {
          $(this).text(cropLabel);
          $(this).attr('data', 'crop')
        }
      })
    }   
	};

  	
	eXo.social.webui.UIImageCropper = UIImageCropper;
})(); 
