package chapter2

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains
import strikt.assertions.isEqualTo
import java.net.URI

val hof = ::extractListData andThen
        ::fetchListContent andThen
        ::renderHtml andThen
        ::createResponse

fun fetchList(request: Request): Response = hof(request)

class E01_AndThenTest {

    @Test
    fun `concat many functions`() {
        val request = Request("GET", URI("http://example.com/zettai/ingduk2/mylist"), "");
        val response = hof(request)

        expectThat(response) {
            get { status }.isEqualTo(200)
            get { this.body }.contains("<td>ingduk2 buy milk</td>");
            get { this.body }.contains("<td>complete mylist</td>");
            get { this.body }.contains("<td>something else</td>");
        }
    }
}