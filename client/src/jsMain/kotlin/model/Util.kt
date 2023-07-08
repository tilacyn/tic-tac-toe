package model

import kotlinx.browser.window
import org.w3c.xhr.XMLHttpRequest

fun basicApiRequest(method: String, path: String) : XMLHttpRequest {
    val req = XMLHttpRequest()
//    req.open(method, "http://localhost:8081/${path}")
    return req
}