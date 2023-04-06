'use strict'

export const sendDeleteRequest = async function(url) {
	try {
		fetch(url);
	} catch(err) {
		console.error(err);
	}
};

export const searchRequest = async function(searchQuery) {
	try {
		const res = await fetch("/search", {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: searchQuery,
		});
		
		if (!res.ok) return;
		
		const data = await res.json();
		return data;

	} catch (err) {
		console.error(err);
	}
}