export const corsHeaders = {
	"Access-Control-Allow-Origin": "*",
	"Access-Control-Allow-Headers": "Content-Type",
	"Access-Control-Allow-Methods": "GET, POST, OPTIONS"
};

export function json(data, status = 200) {
	return new Response(JSON.stringify(data), {
		status,
		headers: { ...corsHeaders, "Content-Type": "application/json" }
	});
}

export function error(message, status = 400) {
	return json({ ok: false, error: message }, status);
}
