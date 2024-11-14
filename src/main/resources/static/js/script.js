console.log("this is js");

const toggleSidebar=()=>{
    if($('.sidebar').is(":visible")){
        $(".sidebar").css("display","none")
        $(".content").css("margin-left","0%");
    }else{
        $(".sidebar").css("display","block")
        $(".content").css("margin-left","20%");
    }
};

const search=()=>{
    //console.log("called")
    let query=$("#search-input").val();
    console.log(query);
    if(query==""){
        $(".search-result").hide();
    }
    else{
    

        let url=`http://localhost:8181/search/${query}`;
        fetch(url).then((response)=>{
            return response.json();
        }).then((data)=>{
            console.log(data);
            let text=`<div class='list-group'>`
            data.forEach((element) => {
                text += `<a href='/user/${element.cid}/contact' class='list-group-item list-group-action'>${element.name}</a>`;
            });

            text+=`</div>`

            $(".search-result").html(text);
            $(".search-result").show()
        })
        
    }
}
