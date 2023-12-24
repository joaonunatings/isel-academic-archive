declare global {
	interface Window {
		env: any,
		apiUrl: string,
		msalClientId: string;
	}
}

export const environment = {
	production: true,
	apiUrl: window.env.apiUrl || "localhost:8080/api/v1/",
	msalClientId: window.env.msalClientId
};
