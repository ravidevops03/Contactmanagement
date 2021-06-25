console.log("This is JS file");

const toggleSidebar = () =>{
      if($(".sidebar").is(":visible")){
            //close the bar

            $(".sidebar").css("display","none");
            $(".content").css("margin-left","0%");
      }else{
            // open the bar
            $(".sidebar").css("display","block");
            $(".content").css("margin-left","20%");
      }
}