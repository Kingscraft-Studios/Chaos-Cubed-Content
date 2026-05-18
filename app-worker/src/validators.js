const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const NAME_MAX = 32;

export function validateUUID(uuid) {
	return uuid && UUID_REGEX.test(uuid);
}

export function validateName(name) {
	return (
		name &&
		typeof name === "string" &&
		name.trim().length > 0 &&
		name.trim().length <= NAME_MAX
	);
}

export async function parseBody(request) {
	try {
		return await request.json();
	} catch {
		return null;
	}
}
