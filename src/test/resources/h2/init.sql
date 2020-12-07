
CREATE TABLE IF NOT EXISTS submit_order (
	id serial PRIMARY KEY,
	content varchar(1024) NOT NULL,
	submit_time integer NOT NULL,
	is_danger boolean default false
);


CREATE TABLE IF NOT EXISTS detect_history (
	id serial PRIMARY KEY,
	order_id integer not null,
	strategy varchar(512) NOT NULL,
	found_suspicion boolean default false,
	process_time integer NOT NULL
);

create index if not exists idx_order_id on detect_history(order_id);