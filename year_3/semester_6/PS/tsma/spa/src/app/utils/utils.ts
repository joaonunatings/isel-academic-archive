export class Utils {

	public static getURLWithQueryParametersOf(url: string, ids?:number[], sortBy?:string[], pageSize?:number, pageNumber?:number) : URL {
		let urlQp = new URL(url)
		if (ids && ids.length > 0) urlQp.searchParams.append("ids",ids.toString())
		if (sortBy && sortBy.length > 0) {
			for (const string of sortBy) {
				urlQp.searchParams.append("sort",string)
			}
		}
		if (pageSize) urlQp.searchParams.append("size",pageSize.toString())
		if (pageNumber) urlQp.searchParams.append("page",pageNumber.toString())
		return urlQp
	}

	public static getElementAndSetVisibility(elemId: string, visibility?: string, opacity?: string) {
		let elem = document.getElementById(elemId)
		if (!elem) return
		if(visibility) elem.style.visibility = visibility
		if(opacity) elem.style.opacity = opacity
	}
}
