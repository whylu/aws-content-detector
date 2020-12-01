
CREATE TABLE IF NOT EXISTS submit_order (
	id serial PRIMARY KEY,
	content varchar(1024) NOT NULL,
	submit_time integer NOT NULL,
	is_danger boolean default false
);

