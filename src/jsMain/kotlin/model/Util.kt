package model

import org.w3c.xhr.XMLHttpRequest

fun basicApiRequest(method: String, path: String) : XMLHttpRequest {
    val req = XMLHttpRequest()
    req.open(method, "/v1/${path}")
    return req
}






