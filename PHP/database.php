<?php 

class Database {
	
	public static $connection = false;
	
	private $host = 'localhost';
	private $port = '5432';
    private $user = 'admin';
    private $pass = 'halfmoon';
    private $db = 'capstone';
	private $type = 'pgsql';
	
	public function __construct() {
		$this->connect();
    }

	public function connect() {
        $dsn = $this->type . ":dbname=" . $this->db . ";host=" . $this->host . ";port=" . $this->port;
        try {
            self::$connection = new PDO($dsn, $this->user, $this->pass);
        } catch (PDOException $e) {
            echo $e->getMessage();
			die();
        }
    }
	
	public function getConnection() {
		return self::$connection;
	}
}
?>