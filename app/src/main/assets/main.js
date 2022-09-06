 $(function() {  //Run once DOM is ready

      $('img:not([data-nolink])').each(function() {  //Iterate through each img element

        $(this).wrap('<a href="' + $(this).attr('src')+'?this_is_a_image' + '">'); //Wrap with link to own src

      });

    });