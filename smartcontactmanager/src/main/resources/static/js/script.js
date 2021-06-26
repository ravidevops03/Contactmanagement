console.log("This is JS file");

const toggleSidebar = () => {
      if ($(".sidebar").is(":visible")) {
            //close the bar

            $(".sidebar").css("display", "none");
            $(".content").css("margin-left", "0%");
      } else {
            // open the bar
            $(".sidebar").css("display", "block");
            $(".content").css("margin-left", "20%");
      }
}

const search = () => {
      // console.log("searching data");

      let query = $("#search-input").val();


      if (query == '') {
            $(".search-result").hide();
      } else {
            console.log(query);
            let url = `http://localhost:8282/search/${query}`;

            // fetch(url)
            //       .then((response) => {
            //             return response.json();
            //       })
            //       .then((data) => {
            //             console.log(data);
            //       })

            axios
                  .get(url)
                  .then(response => {
                        // console.log(response.data);
                        let text = `<div class='list-group'>`;
                        response.data.forEach((contact) => {
                              console.log(contact.name);
                              text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`
                        });
                        text += `</div>`;
                        $(".search-result").html(text);
                  })
                  .catch(error => console.error(error));

            $(".search-result").show();

      }
}