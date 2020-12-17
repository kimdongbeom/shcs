$(document).ready(function() {

    // window.onmessage = function(e) {
    //     debugger;
    //     if (e.origin !== "http://local.media.com:8080") {
    //         return;
    //     }
    //     const { action, key, value } = e.data
    //     if (action == 'save'){
    //         window.localStorage.setItem(key, JSON.stringify(value))
    //     } else if (action == 'get') {
    //         const keyValue = window.localStorage.getItem(key);
    //         console.log(key, keyValue);
    //         e.source.postMessage({
    //             action: 'returnData',
    //             key,
    //             keyValue
    //         }, '*')
    //     }
    // }
    const domains = [
        "http://local.media.com:8080",
        "http://local.nhnent.com:8082",
        "http://local.toast.com:8088"
    ]

    window.onmessage = (e) => {
        debugger;
        if (!domains.includes(e.origin)) {
            return;
        }
        const payload = JSON.parse(e.data);
        switch(payload.method) {
            case 'set':
                localStorage.setItem(payload.key, JSON.stringify(payload.data));
                break;
            case 'get':
                const parent = window.parent;
                debugger;
                // var date = new Date();
                // date.setTime(date.getTime() + 1*24*60*60*1000);
                // document.cookie="BID=COKIEBID" + date.toUTCString() + ';path=/';
                //
                // var value = document.cookie.match('(^|;) ?' + "BID" + '=([^;]*)(;|$)');
                // console.log(value);

                const data = localStorage.getItem(payload.key);
                const returnPayload = {
                    method: 'storage#get',
                    data: data
                }
                parent.postMessage(JSON.stringify(returnPayload), '*');
                break;
        }
    };




    // window.addEventListener("message", messageHandler, false);
    // function messageHandler(event) {
    //     debugger;
    //     if (!domains.includes(event.origin))
    //         return;
    //     const { action, key, value } = event.data
    //     if (action == 'save'){
    //         window.localStorage.setItem(key, JSON.stringify(value))
    //     } else if (action == 'get') {
    //         const keyValue = window.localStorage.getItem(key);
    //         event.source.postMessage({
    //             action: 'returnData',
    //             key,
    //             keyValue
    //         }, '*')
    //     }
    // }
});